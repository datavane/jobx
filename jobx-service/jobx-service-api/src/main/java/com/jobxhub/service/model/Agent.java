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
import com.jobxhub.service.entity.AgentEntity;
import com.jobxhub.service.entity.GroupEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Agent implements Serializable {

    private Long agentId;
    private String agentName;

    private String machineId;

    //代理执行器的Id
    private Long proxyId;

    private String host;
    //agent系统平台,0:其他,1:unix内核系统,2:window
    private Integer platform;
    private Integer port;
    private String password;
    private Boolean warning;

    private String email;
    private String mobile;

    private Integer status;//1通讯成功,0:失败失联,2:密码错误
    private Date notifyTime;//失败后发送通知告警的时间
    private String comment;
    private Date updateTime;

    private List<GroupEntity> groups = new ArrayList<GroupEntity>(0);

    private Integer taskCount;

    private List<User> users = new ArrayList<User>();

    private Boolean proxy = false;//是否代理

    public static Function<AgentEntity,Agent> transferModel = new Function<AgentEntity, Agent>() {
        @Override
        public Agent apply(AgentEntity input) {
            Agent agent = new Agent();
            BeanUtils.copyProperties(input,agent);
            return agent;
        }
    };

    public static Function<Agent,AgentEntity> transferEntity = new Function<Agent,AgentEntity>() {
        @Override
        public AgentEntity apply(Agent input) {
            AgentEntity agentEntity = new AgentEntity();
            BeanUtils.copyProperties(input,agentEntity);
            return agentEntity;
        }
    };


}
