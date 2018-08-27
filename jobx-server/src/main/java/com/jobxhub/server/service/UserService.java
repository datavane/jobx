/**
 * Copyright (c) 2015 The JobX Project
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


package com.jobxhub.server.service;

import com.google.common.collect.Lists;
import com.jobxhub.common.Constants;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.DigestUtils;
import com.jobxhub.common.util.IOUtils;
import com.jobxhub.server.domain.UserBean;
import com.jobxhub.server.dao.UserDao;
import com.jobxhub.server.support.JobXTools;
import com.jobxhub.server.tag.PageBean;
import com.jobxhub.server.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 *
 * Created by ChenHui on 2016/2/18.
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ConfigService configService;

    @Autowired
    private UserAgentService userAgentService;

    public int login(HttpServletRequest request, String userName, String password) throws IOException {

        HttpSession httpSession = request.getSession();

        User user = null;
        UserBean userBean = userDao.getByName(userName);
        if (userBean != null) {
            user = User.transfer.apply(userBean);
        }
        if (user == null) return 500;

        //拿到数据库的数据盐
        byte[] salt = DigestUtils.decodeHex(user.getSalt());
        String saltPassword = DigestUtils.encodeHex(DigestUtils.sha1(password.toUpperCase().getBytes(), salt, 1024));

        if (saltPassword.equals(user.getPassword())) {
            if (user.getRoleId() == 999L) {
                httpSession.setAttribute(Constants.PARAM_PERMISSION_KEY, true);
                //超管拥有所有的用户执行身份...
                user.setExecUser(configService.getExecUser());
            } else {
                httpSession.setAttribute(Constants.PARAM_PERMISSION_KEY, false);
            }
            JobXTools.logined(request, user);
            return 200;
        } else {
            return 500;
        }
    }

    public User getByName(String userName) {
        UserBean userBean = userDao.getByName(userName);
        if (userBean != null) {
            return User.transfer.apply(userBean);
        }
        return null;
    }

    public PageBean getPageBean(PageBean pageBean) {
        List<UserBean> userList = userDao.getByPageBean(pageBean);
        int count = userDao.getCount(pageBean.getFilter());
        pageBean.setResult(Lists.transform(userList, User.transfer));
        pageBean.setTotalRecord(count);
        return pageBean;
    }

    public void addUser(User user) {
        UserBean userBean = UserBean.transfer.apply(user);
        String salter = CommonUtils.uuid(16);
        userBean.setSalt(salter);
        byte[] salt = DigestUtils.decodeHex(salter);
        String saltPassword = DigestUtils.encodeHex(DigestUtils.sha1(userBean.getPassword().getBytes(), salt, 1024));
        userBean.setPassword(saltPassword);
        userBean.setCreateTime(new Date());
        userDao.save(userBean);
        user.setUserId(userBean.getUserId());
    }

    public User getUserById(Long id) {
        UserBean userBean = userDao.getById(id);
        if (userBean != null) {
            return User.transfer.apply(userBean);
        }
        return null;
    }

    public void updateUser(HttpSession session, User user) {
        if (!JobXTools.isPermission(session)) {
            userAgentService.update(user.getUserId(), user.getAgentIds());
        }
        userDao.update(UserBean.transfer.apply(user));
    }

    public void uploadImg(Long userId, File file) throws IOException {
        byte[] bytes = IOUtils.toByteArray(file);
        userDao.uploadImg(userId, bytes);
    }

    public String editPassword(Long id, String pwd0, String pwd1, String pwd2) {
        User user = getUserById(id);
        byte[] salt = DigestUtils.decodeHex(user.getSalt());
        byte[] hashPassword = DigestUtils.sha1(pwd0.getBytes(), salt, 1024);
        pwd0 = DigestUtils.encodeHex(hashPassword);
        if (pwd0.equals(user.getPassword())) {
            if (pwd1.equals(pwd2)) {
                byte[] hashPwd = DigestUtils.sha1(pwd1.getBytes(), salt, 1024);
                user.setPassword(DigestUtils.encodeHex(hashPwd));
                userDao.updatePassword(user.getUserId(), user.getPassword());
                return "true";
            } else {
                return "two";
            }
        } else {
            return "one";
        }
    }

    public boolean existsName(String name) {
        Map<String,Object> map = new HashMap<String, Object>(0);
        map.put("user_name",name);
        return userDao.getCount(map) > 0;
    }

    public List<String> getExecUser(Long userId) {
        UserBean user = userDao.getById(userId);
        if (user.getRoleId() == 999L) {
            return configService.getExecUser();
        }else {
            String execUser = userDao.getExecUser(userId);
            if (CommonUtils.notEmpty(execUser)) {
                return Arrays.asList(execUser.split(","));
            }
        }
        return Collections.EMPTY_LIST;
    }
}


