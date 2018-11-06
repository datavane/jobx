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


package com.jobxhub.core.entity;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

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

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCronExp() {
        return cronExp;
    }

    public void setCronExp(String cronExp) {
        this.cronExp = cronExp;
    }

    public String getExecUser() {
        return execUser;
    }

    public void setExecUser(String execUser) {
        if (execUser!=null) {
            this.execUser = execUser.trim();
        }
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSuccessExit() {
        return successExit;
    }

    public void setSuccessExit(String successExit) {
        this.successExit = successExit;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getRedo() {
        return redo;
    }

    public void setRedo(Integer redo) {
        this.redo = redo;
    }

    public Integer getRunCount() {
        return runCount;
    }

    public void setRunCount(Integer runCount) {
        this.runCount = runCount;
    }

    public Boolean getCluster() {
        return cluster;
    }

    public void setCluster(Boolean cluster) {
        this.cluster = cluster;
    }

    public Integer getJobType() {
        return jobType;
    }

    public void setJobType(Integer jobType) {
        this.jobType = jobType;
    }

    public Integer getCreateType() {
        return createType;
    }

    public void setCreateType(Integer createType) {
        this.createType = createType;
    }

    public Boolean getWarning() {
        return warning;
    }

    public void setWarning(Boolean warning) {
        this.warning = warning;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Boolean getPause() {
        return pause;
    }

    public void setPause(Boolean pause) {
        this.pause = pause;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getOperateUname() {
        return operateUname;
    }

    public void setOperateUname(String operateUname) {
        this.operateUname = operateUname;
    }

    public Integer getAlarmCode() {
        return alarmCode;
    }

    public void setAlarmCode(Integer alarmCode) {
        this.alarmCode = alarmCode;
    }

    public Integer getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(Integer alarmType) {
        this.alarmType = alarmType;
    }
}
