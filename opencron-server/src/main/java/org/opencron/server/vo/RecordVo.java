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


package org.opencron.server.vo;

import org.opencron.server.domain.User;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RecordVo implements Serializable {

    private Long recordId;
    private Long jobId;
    private String command;
    private Integer returnCode;
    private Integer success;
    private Date startTime;
    private Date endTime;
    private Integer execType;
    private String message;
    private Integer redoCount;
    private Integer status;
    private Long groupId;

    private String cronExp;
    private Long userId;
    private String operateUname;
    private Long agentId;
    private String agentName;
    private String jobName;
    private String ip;
    private User user;
    private Integer redo;
    private Integer runCount;

    /**
     * 重跑记录对象的父记录
     */
    private Long parentId;

    /**
     * 任务类型(0:单一任务,1:流程任务)
     */
    private Integer jobType;

    private JobVo job;

    //重跑子记录
    private List<RecordVo> childRecord;

    //流程子任务
    private List<RecordVo> childJob;

    //是否为流程任务的最后一个子任务
    private Boolean lastChild;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Integer getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getExecType() {
        return execType;
    }

    public void setExecType(Integer execType) {
        this.execType = execType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getRedoCount() {
        return redoCount;
    }

    public void setRedoCount(Integer redoCount) {
        this.redoCount = redoCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getCronExp() {
        return cronExp;
    }

    public void setCronExp(String cronExp) {
        this.cronExp = cronExp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOperateUname() {
        return operateUname;
    }

    public void setOperateUname(String operateUname) {
        this.operateUname = operateUname;
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

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getJobType() {
        return jobType;
    }

    public void setJobType(Integer jobType) {
        this.jobType = jobType;
    }

    public JobVo getJob() {
        return job;
    }

    public void setJob(JobVo job) {
        this.job = job;
    }

    public List<RecordVo> getChildRecord() {
        return childRecord;
    }

    public void setChildRecord(List<RecordVo> childRecord) {
        this.childRecord = childRecord;
    }

    public List<RecordVo> getChildJob() {
        return childJob;
    }

    public void setChildJob(List<RecordVo> childJob) {
        this.childJob = childJob;
    }

    public Boolean getLastChild() {
        return lastChild;
    }

    public void setLastChild(Boolean lastChild) {
        this.lastChild = lastChild;
    }
}
