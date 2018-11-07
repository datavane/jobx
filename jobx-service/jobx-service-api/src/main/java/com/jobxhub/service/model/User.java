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
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    public User(){}

    public static Function<UserEntity,User> transferModel = new Function<UserEntity, User>() {
        @Override
        public User apply(UserEntity input) {
            User user = new User();
            BeanUtils.copyProperties(user,user);
            if (user!=null&&user.getExecUser()!=null) {
                user.setExecUser(Arrays.asList(input.getExecUser().split(",")));
            }
            return user;
        }
    };

    public static Function<User,UserEntity> transferEntity = new Function<User,UserEntity>() {
        @Override
        public UserEntity apply(User input) {
            UserEntity userEntity = new UserEntity();
            BeanUtils.copyProperties(input,userEntity);
            if (input!=null&&input.getExecUser()!=null) {
                userEntity.setExecUser(StringUtils.join(input.getExecUser(),","));
            }
            return userEntity;
        }
    };

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public byte[] getHeaderPic() {
        return headerPic;
    }

    public void setHeaderPic(byte[] headerPic) {
        this.headerPic = headerPic;
    }

    public String getPicExtName() {
        return picExtName;
    }

    public void setPicExtName(String picExtName) {
        this.picExtName = picExtName;
    }

    public String getHeaderPath() {
        return headerPath;
    }

    public void setHeaderPath(String headerPath) {
        this.headerPath = headerPath;
    }

    public List<Long> getAgentIds() {
        return agentIds;
    }

    public void setAgentIds(List<Long> agentIds) {
        this.agentIds = agentIds;
    }

    public List<String> getExecUser() {
        return execUser;
    }

    public void setExecUser(List<String> execUser) {
        this.execUser = execUser;
    }
}
