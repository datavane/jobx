/**
 * Copyright 2016 benjobs
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.opencron.server.service;

import org.opencron.common.utils.CommonUtils;
import org.opencron.server.dao.QueryDao;
import org.opencron.server.dao.UploadDao;
import org.opencron.server.domain.Role;
import org.opencron.server.domain.User;
import org.opencron.server.tag.PageBean;
import org.opencron.common.utils.Digests;
import org.opencron.common.utils.Encodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by ChenHui on 2016/2/18.
 */
@Service
public class UserService {

    @Autowired
    private QueryDao queryDao;

    @Autowired
    private UploadDao uploadDao;

    public PageBean queryUser(PageBean pageBean) {
        String sql = "SELECT U.*,R.roleName FROM T_USER AS U LEFT JOIN T_ROLE R ON U.roleId = R.roleId";
        queryDao.getPageBySql(pageBean, User.class, sql);
        return pageBean;
    }

    public List<Role> getRoleGroup() {
        return queryDao.getAll(Role.class);
    }

    public void addUser(User user) {
        String saltstr = CommonUtils.uuid(16);
        user.setSalt(saltstr);
        byte[] salt = Encodes.decodeHex(saltstr);
        String saltPassword = Encodes.encodeHex(Digests.sha1(user.getPassword().getBytes(), salt, 1024));
        user.setPassword(saltPassword);
        user.setCreateTime(new Date());
        queryDao.save(user);
    }

    public User getUserById(Long id) {
        return queryDao.get(User.class, id);
    }

    public void updateUser(User user) {
        queryDao.save(user);
    }

    @Transactional(readOnly = false)
    public User uploadimg(File file, Long userId) throws IOException {
        return uploadDao.uploadimg(file,userId);
    }

    public User queryUserById(Long id) {
        String sql = "SELECT U.*,R.roleName FROM T_USER AS U LEFT JOIN T_ROLE R ON U.roleId = R.roleId WHERE userId = ?";
        return queryDao.sqlUniqueQuery(User.class, sql, id);
    }

    public String editPwd(Long id, String pwd0, String pwd1, String pwd2) {
        User user = getUserById(id);
        byte[] salt = Encodes.decodeHex(user.getSalt());
        byte[] hashPassword = Digests.sha1(pwd0.getBytes(), salt, 1024);
        pwd0 = Encodes.encodeHex(hashPassword);
        if (pwd0.equals(user.getPassword())) {
            if (pwd1.equals(pwd2)) {
                byte[] hashPwd = Digests.sha1(pwd1.getBytes(), salt, 1024);
                user.setPassword(Encodes.encodeHex(hashPwd));
                queryDao.save(user);
                return "success";
            } else {
                return "two";
            }
        } else {
            return "one";
        }
    }

    public String checkName(String name) {
        String sql = "SELECT COUNT(1) FROM T_USER WHERE userName=?";
        return (queryDao.getCountBySql(sql, name)) > 0L ? "no" : "yes";
    }


}
