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

import com.jobxhub.service.model.Agent;
import com.jobxhub.service.model.User;
import lombok.Data;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Data
public class JobEntity implements Serializable {

    /**
     * 主键ID
     */
    private Long jobId;

    /**
     * 作业名称
     */
    private String jobName;

    /**
     * 作业类型
     */
    private Integer jobType;

    /**
     * 时间表达式
     */
    private String cronExp;

    /**
     * 执行身份
     */
    private String execUser;

    /**
     * 执行命令
     */
    private String command;

    /**
     * 描述信息
     */
    private String comment;

    /**
     * 任务成功code
     */
    private String successExit;

    /**
     * 重跑次数,0:不重跑
     */
    private Integer runCount;

    /**
     * 作业创建类型
     */
    private Integer createType;

    /**
     * 任务是否托管
     */
    private Boolean pause;

    /**
     * 任务失败是否告警
     */
    private Boolean alarm;

    /**
     * 钉钉机器人URL
     */
    private String alarmDingURL;
    /**
     * 钉钉@用户手机号
     */
    private String alarmDingAtUser;

    /**
     * 收件邮箱地址
     */
    private String alarmEmail;

    /**
     * 短信通道商URL
     */
    private String alarmSms;

    /**
     * 短信模板
     */
    private String alarmSmsTemplate;

    /**
     * 超时时间
     */
    private Integer timeout;

    /**
     * 任务所属的用户
     */
    private Long userId;

    /**
     * 该任务运作在哪台agent上
     */
    private Long agentId;


    private Date updateTime;

    @Transient
    private String agentName;

    /**
     * 回调URL
     */
    private String callbackURL;

    /**
     * api调用的认证token
     */
    private String token;

    private String sn;

    @Transient
    private Agent agent;

    @Transient
    private User user;

    private String operateUname;


    public void setExecUser(String execUser) {
        if (execUser!=null) {
            this.execUser = execUser.trim();
        }
    }

}
