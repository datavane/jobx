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

import org.opencron.common.utils.DigestUtils;
import org.opencron.server.dao.QueryDao;
import org.opencron.server.domain.Config;
import org.opencron.server.domain.Role;
import org.opencron.server.domain.User;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ChenHui on 2016/2/17.
 */
@Service
public class ConfigService {

    @Autowired
    private QueryDao queryDao;

    @Autowired
    private UserService userService;

    public Config getSysConfig() {
        return queryDao.sqlUniqueQuery(Config.class, "SELECT * FROM T_CONFIG WHERE configId = 1");
    }

    public void update(Config config) {
        queryDao.save(config);
    }

    public void initDataBase() {
        long count = queryDao.getCountBySql("SELECT COUNT(1) FROM T_CONFIG");
        if (count==0) {

            Session session = queryDao.getSessionFactory().openSession();
            session.getTransaction().begin();

            Config config = new Config();
            config.setConfigId(1L);
            config.setSenderEmail("you_mail_name");
            config.setSmtpHost("smtp.exmail.qq.com");
            config.setSmtpPort(465);
            config.setPassword("your_mail_pwd");
            config.setSendUrl("http://your_url");
            config.setSpaceTime(30);
            session.save(config);

            Role role = new Role();
            role.setRoleId(1L);
            role.setRoleName("admin");
            role.setDescription("view privileges");
            session.save(role);

            Role superRole = new Role();
            superRole.setRoleId(999L);
            superRole.setRoleName("superAdmin");
            superRole.setDescription("all privileges");
            session.save(superRole);

            session.getTransaction().commit();

            User user = new User();
            user.setUserName("opencron");
            user.setPassword(DigestUtils.md5Hex("opencron").toUpperCase());
            user.setRoleId(999L);
            user.setRealName("opencron");
            user.setEmail("benjobs@qq.com");
            user.setQq("benjobs@qq.com");
            user.setContact("13800138000");
            userService.addUser(user);

        }

    }
}
