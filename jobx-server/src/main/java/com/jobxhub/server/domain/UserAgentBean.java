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
import com.jobxhub.server.dto.UserAgent;
import org.springframework.beans.BeanUtils;

import javax.persistence.Transient;

public class UserAgentBean {

    private Long id;
    private Long userId;
    @Transient
    private String userName;
    private Long agentId;
    @Transient
    private String agentName;
    @Transient
    private String agentHost;

    public UserAgentBean() {}

    public UserAgentBean(UserAgent userAgent){
        BeanUtils.copyProperties(userAgent,this);
    }

    public static Function<? super UserAgent, ? extends UserAgentBean> transfer = new Function<UserAgent, UserAgentBean>() {
        @Override
        public UserAgentBean apply(UserAgent input) {
            return new UserAgentBean(input);
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentHost() {
        return agentHost;
    }

    public void setAgentHost(String agentHost) {
        this.agentHost = agentHost;
    }
}
