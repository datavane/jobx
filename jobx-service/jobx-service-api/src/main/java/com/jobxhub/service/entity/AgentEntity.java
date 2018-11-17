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


package com.jobxhub.service.entity;

import lombok.Data;

import java.util.Date;
import java.util.Objects;


@Data
public class AgentEntity {

    private Long agentId;

    //执行器机器的唯一id(当前取的是机器的MAC地址)
    private String machineId;

    //代理执行器的Id
    private Long proxyId;

    private String host;
    private Integer platform;
    private Integer port;
    private String name;
    private String password;
    private Boolean warning;

    private String email;
    private String mobile;

    private Integer status;//1通讯成功,0:失败失联,2:密码错误
    private Date notifyTime;//失败后发送通知告警的时间
    private String comment;
    private Date updateTime;

}
