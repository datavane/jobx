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


package com.jobxhub.server.domain;

import com.google.common.base.Function;
import com.jobxhub.common.util.StringUtils;
import com.jobxhub.server.dto.User;
import org.springframework.beans.BeanUtils;

import javax.persistence.Transient;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by benjobs on 14-6-25.
 */
public class UserBean {

    private Long userId;

    private Long roleId;

    private String userName;

    private String password;

    private String salt;

    private String realName;

    private String contact;

    private String email;

    private String qq;

    private Date createTime;

    private Date modifyTime;

    @Transient
    private String roleName;

    private byte[] headerPic;

    //头像文件的后缀名字
    private String picExtName;

    private String execUser;

    public UserBean() {}

    public UserBean(User user){
        BeanUtils.copyProperties(user,this);
        if (user!=null&&user.getExecUser()!=null) {
            this.execUser = StringUtils.join(user.getExecUser(),",");
        }
    }

    public static Function<? super User, ? extends UserBean> transfer = new Function<User, UserBean>() {
        @Override
        public UserBean apply(User input) {
            return new UserBean(input);
        }
    };

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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

    public String getExecUser() {
        return execUser;
    }

    public void setExecUser(String execUser) {
        this.execUser = execUser;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserBean user = (UserBean) obj;
        return userId != null ? userId.equals(user.userId) : user.userId == null;
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "userId=" + userId +
                ", roleId=" + roleId +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", realName='" + realName + '\'' +
                ", contact='" + contact + '\'' +
                ", email='" + email + '\'' +
                ", qq='" + qq + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", roleName='" + roleName + '\'' +
                ", headerPic=" + Arrays.toString(headerPic) +
                ", picExtName='" + picExtName + '\'' +
                ", execUser='" + execUser + '\'' +
                '}';
    }
}
