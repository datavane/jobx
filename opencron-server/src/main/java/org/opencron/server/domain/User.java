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


package org.opencron.server.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

/**
 * Created by benjobs on 14-6-25.
 */
@Entity
@Table(name = "T_USER")
public class User implements Serializable {

    @Id
    @GeneratedValue
    private Long userId;

    private Long roleId;

    private String userName;

    private String password;

    private String salt;

    private String agentIds;

    private String realName;

    private String contact;

    private String email;

    private String qq;

    private Date createTime;

    private Date modifyTime;

    @Transient
    private String roleName;

    @Lob
    @Column(name = "headerpic", columnDefinition = "LONGBLOB")
    private Blob headerpic;

    @Transient
    private String headerPath;

    //头像文件的后缀名字
    private  String picExtName;

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

    public String getAgentIds() {
        return agentIds;
    }

    public void setAgentIds(String agentIds) {
        this.agentIds = agentIds;
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

    public Blob getHeaderpic() {
        return headerpic;
    }

    public void setHeaderpic(Blob headerpic) {
        this.headerpic = headerpic;
    }

    public String getHeaderPath() {
        return headerPath;
    }

    public void setHeaderPath(String headerPath) {
        this.headerPath = headerPath;
    }

    public String getPicExtName() {
        return picExtName;
    }

    public void setPicExtName(String picExtName) {
        this.picExtName = picExtName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId != null ? userId.equals(user.userId) : user.userId == null;
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", roleId=" + roleId +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", agentIds='" + agentIds + '\'' +
                ", realName='" + realName + '\'' +
                ", contact='" + contact + '\'' +
                ", email='" + email + '\'' +
                ", qq='" + qq + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", roleName='" + roleName + '\'' +
                ", headerpic=" + headerpic +
                ", headerPath='" + headerPath + '\'' +
                ", picExtName='" + picExtName + '\'' +
                '}';
    }
}
