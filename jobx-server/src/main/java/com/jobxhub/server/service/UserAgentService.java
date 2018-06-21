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
import com.jobxhub.server.domain.UserAgentBean;
import com.jobxhub.server.dao.UserAgentDao;
import com.jobxhub.server.dto.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAgentService {

    @Autowired
    private UserAgentDao userAgentDao;

    public void update(Long userId, List<Long> agentIds) {
        userAgentDao.delete(userId);
        for (Long id:agentIds) {
            userAgentDao.save(userId,id);
        }
    }

    public List<UserAgent> getUserAgent(Long userId) {
        List<UserAgentBean> userAgent = userAgentDao.getUserAgent(userId);
        return Lists.transform(userAgent,UserAgent.transfer);
    }

    public void save(UserAgent userAgent) {
        userAgentDao.save(userAgent.getUserId(),userAgent.getAgentId());
    }

}