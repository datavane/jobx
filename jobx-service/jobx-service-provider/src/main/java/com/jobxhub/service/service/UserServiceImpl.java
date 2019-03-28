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


package com.jobxhub.service.service;

import org.apache.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.DigestUtils;
import com.jobxhub.common.util.IOUtils;
import com.jobxhub.service.api.ConfigService;
import com.jobxhub.service.api.UserAgentService;
import com.jobxhub.service.api.UserService;
import com.jobxhub.service.entity.UserEntity;
import com.jobxhub.service.dao.UserDao;
import com.jobxhub.service.model.User;
import com.jobxhub.service.vo.PageBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by ChenHui on 2016/2/18.
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ConfigService configService;

    @Autowired
    private UserAgentService userAgentService;

    @Override
    public User login(String userName, String password) {
        UserEntity userEntity = userDao.getByName(userName);
        if (userEntity == null) {
            return null;
        }
        //拿到数据库的数据盐
        byte[] salt = DigestUtils.decodeHex(userEntity.getSalt());
        String saltPassword = DigestUtils.encodeHex(DigestUtils.sha1(password.toUpperCase().getBytes(), salt, 1024));
        if (saltPassword.equals(userEntity.getPassword())) {
            return User.transferModel.apply(userEntity);
        }
        return null;
    }

    @Override
    public boolean addUser(User user) {
        UserEntity userEntity = User.transferEntity.apply(user);
        String salter = CommonUtils.uuid(16);
        userEntity.setSalt(salter);
        byte[] salt = DigestUtils.decodeHex(salter);
        String saltPassword = DigestUtils.encodeHex(DigestUtils.sha1(userEntity.getPassword().getBytes(), salt, 1024));
        userEntity.setPassword(saltPassword);
        userEntity.setCreateTime(new Date());
        int count = userDao.save(userEntity);
        if (count == 1) {
            user.setUserId(userEntity.getUserId());
            return true;
        }
        return false;
    }

    @Override
    public PageBean getPageBean(PageBean pageBean) {
        List<UserEntity> userList = userDao.getByPageBean(pageBean);
        int count = userDao.getCount(pageBean.getFilter());
        pageBean.setResult(Lists.transform(userList, User.transferModel));
        pageBean.setTotalRecord(count);
        return pageBean;
    }

    @Override
    public boolean updateUser(User user) {
        userAgentService.update(user.getUserId(), user.getAgentIds());
        return userDao.update(User.transferEntity.apply(user)) == 1;
    }

    @Override
    public boolean uploadImg(Long userId, File file) {
        try {
            byte[] bytes = IOUtils.toByteArray(file);
            return userDao.uploadImg(userId, bytes) == 1;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean editPassword(Long id, String currPassword, String newPassword) {
        User user = getById(id);
        byte[] salt = DigestUtils.decodeHex(user.getSalt());
        byte[] hashPassword = DigestUtils.sha1(currPassword.getBytes(), salt, 1024);
        currPassword = DigestUtils.encodeHex(hashPassword);
        if (currPassword.equals(user.getPassword())) {
            byte[] hashPwd = DigestUtils.sha1(newPassword.getBytes(), salt, 1024);
            user.setPassword(DigestUtils.encodeHex(hashPwd));
            userDao.updatePassword(user.getUserId(), user.getPassword());
            return true;
        } else {
            return false;//原来密码不正确
        }
    }

    @Override
    public boolean existsName(String name) {
        Map<String, Object> map = new HashMap<String, Object>(0);
        map.put("user_name", name);
        return userDao.getCount(map) > 0;
    }

    @Override
    public List<String> getExecUser(Long userId) {
        return configService.getExecUser();
    }

    private User getById(Long id) {
        UserEntity userEntity = userDao.getById(id);
        if (userEntity != null) {
            return User.transferModel.apply(userEntity);
        }
        return null;
    }
}


