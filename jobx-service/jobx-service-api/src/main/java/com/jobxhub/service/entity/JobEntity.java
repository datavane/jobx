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

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Data
public class JobEntity implements Serializable {
    private Long jobId;
    private Long agentId;

    private String jobName;
    private String cronExp;

    /**
     * 当前作业的执行身份
     */
    private String execUser;
    private String command;

    private String comment;
    private String successExit;
    private Long userId;
    private Date updateTime;
    private Integer redo;
    private Integer runCount;

    /**
     * job是否是分布式部署
     */
    private Boolean cluster = false;

    /**
     * 0:作业
     * 1:工作流
     */
    private Integer jobType;

    /**
     * 创建类型(1:正常简单任务创建,2:工作流子任务创建)
     */
    private Integer createType;

    private Boolean warning;

    private String mobile;

    //任务是否暂停(true:已经暂停,false:未暂停)
    private Boolean pause = false;

    private String email;

    //运行超时的截止时间
    private Integer timeout;

    private String token;//api调用的认证token

    @Transient
    private String agentName;

    @Transient
    private String operateUname;

    private Integer alarmCode;//告警码
    private Integer alarmType;//告警方式


    public void setExecUser(String execUser) {
        if (execUser!=null) {
            this.execUser = execUser.trim();
        }
    }

}
