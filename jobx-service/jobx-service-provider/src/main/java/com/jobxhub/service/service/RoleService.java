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

import com.google.common.collect.Lists;
import com.jobxhub.service.entity.RoleEntity;
import com.jobxhub.service.dao.RoleDao;
import com.jobxhub.service.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RoleService {


    @Autowired
    private RoleDao roleDao;

    public List<Role> getAll() {
        List<RoleEntity> roles = roleDao.getAll();
        return Lists.transform(roles,Role.transferModel);
    }

    public Role getById(Long roleId) {
        RoleEntity role = roleDao.getById(roleId);
        if (role == null) return null;
        return Role.transferModel.apply(role);
    }

    public void addRole(Role role) {
        RoleEntity roleEntity = Role.transferEntity.apply(role);
        roleDao.save(roleEntity);
        role.setRoleId(role.getRoleId());
    }
}
