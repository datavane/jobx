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
package com.jobxhub.service.model;

import com.google.common.base.Function;
import com.jobxhub.common.util.StringUtils;
import com.jobxhub.service.entity.UserEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
public class User implements Serializable {

    private Long userId;
    private String userName;
    private String password;
    private String salt;
    private Long roleId;
    private String roleName;
    private String realName;
    private String contact;
    private String email;
    private String qq;
    private Date createTime;
    private Date modifyTime;
    private byte[] headerPic;
    private String picExtName;
    private String headerPath;
    private List<Long> agentIds;
    private List<String> execUser;

    public User() {
    }

    public static Function<UserEntity, User> transferModel = new Function<UserEntity, User>() {
        @Override
        public User apply(UserEntity input) {
            User user = new User();
            BeanUtils.copyProperties(input, user);
            if (user != null && user.getExecUser() != null) {
                user.setExecUser(Arrays.asList(input.getExecUser().split(",")));
            }
            return user;
        }
    };

    public static Function<User, UserEntity> transferEntity = new Function<User, UserEntity>() {
        @Override
        public UserEntity apply(User input) {
            UserEntity userEntity = new UserEntity();
            BeanUtils.copyProperties(input, userEntity);
            if (input != null && input.getExecUser() != null) {
                userEntity.setExecUser(StringUtils.join(input.getExecUser(), ","));
            }
            return userEntity;
        }
    };

}
