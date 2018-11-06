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


package com.jobxhub.core.model;

import com.google.common.base.Function;
import com.jobxhub.core.entity.RoleEntity;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * Created by ChenHui on 2016/2/17.
 */
public class Role implements Serializable {

    private Long roleId;

    private String roleName;

    private String description;


    public static Function<RoleEntity,Role> transferModel = new Function<RoleEntity, Role>() {
        @Override
        public Role apply(RoleEntity input) {
            Role role = new Role();
            BeanUtils.copyProperties(input,role);
            return role;
        }
    };


    public static Function<Role,RoleEntity> transferEntity = new Function<Role,RoleEntity>() {
        @Override
        public RoleEntity apply(Role input) {
            RoleEntity model = new RoleEntity();
            BeanUtils.copyProperties(input,model);
            return model;
        }
    };

    public Role(){}

    public Role(RoleEntity roleBean){
        BeanUtils.copyProperties(roleBean,this);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
