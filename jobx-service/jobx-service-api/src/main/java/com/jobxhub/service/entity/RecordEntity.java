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

import java.io.Serializable;
import java.util.Date;

@Data
public class RecordEntity implements Serializable {

    private Long recordId;
    private Long jobId;
    private Long agentId;
    private Long userId;
    private String command;
    private String execUser;
    private Integer returnCode;
    private Integer success;
    private Date startTime;
    private Date endTime;
    private Integer execType;
    private Integer status;
    private String pid;
    /**
     * 重跑记录对象的父记录
     */
    private Long parentId;
    private Integer redoNum;
    private Long groupId;
    /**
     * 任务类型(0:单一任务,1:流程任务)
     */
    private Integer jobType;

    private String jobName;
    private String agentName;
    private String host;
    private String cronExp;
    private String operateUname;


}
