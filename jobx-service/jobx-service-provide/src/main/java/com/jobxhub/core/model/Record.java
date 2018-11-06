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


package com.jobxhub.core.model;


import com.google.common.base.Function;
import com.jobxhub.common.Constants;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.core.entity.RecordEntity;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Record implements Serializable {

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
    private String message;

    private Integer status;
    private String pid;
    private Long parentId;
    private Integer redoNum;//第几次重跑
    private Integer redoCount;//共重跑次数

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
    private String queryDate;

    private List<Record> redoList = new ArrayList<Record>(0);

    public static Function<RecordEntity,Record> transferModel = new Function<RecordEntity, Record>() {
        @Override
        public Record apply(RecordEntity input) {
            Record record = new Record();
            BeanUtils.copyProperties(input,record);
            return record;
        }
    };

    public static Function<Record,RecordEntity> transferEntity = new Function<Record,RecordEntity>() {
        @Override
        public RecordEntity apply(Record input) {
            RecordEntity model = new RecordEntity();
            BeanUtils.copyProperties(input,model);
            return model;
        }
    };

    public Record(){}

    public Record(Job job, Constants.ExecType execType, Constants.JobType jobType) {
        this.setJobId(job.getJobId());
        this.setJobType(jobType.getCode());
        this.setJobName(job.getJobName());
        this.setExecType(execType.getStatus());
        this.setAgentId(job.getAgentId());
        this.setUserId(job.getUserId());
        this.setCommand(job.getCommand());//执行的命令
        this.setExecUser(job.getExecUser());
        this.setSuccess(Constants.ResultStatus.SUCCESSFUL.getStatus());
        this.setStatus(Constants.RunStatus.RUNNING.getStatus());//任务还未完成
        this.setPid(CommonUtils.uuid());
        this.start();
    }

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

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getExecUser() {
        return execUser;
    }

    public void setExecUser(String execUser) {
        this.execUser = execUser;
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

    public void start(){
        setStartTime(new Date());
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void end(){
        setEndTime(new Date());
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Integer getRedoNum() {
        return redoNum;
    }

    public void setRedoNum(Integer redoNum) {
        this.redoNum = redoNum;
    }

    public Integer getRedoCount() {
        return redoCount;
    }

    public void setRedoCount(Integer redoCount) {
        this.redoCount = redoCount;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Integer getJobType() {
        return jobType;
    }

    public void setJobType(Integer jobType) {
        this.jobType = jobType;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getCronExp() {
        return cronExp;
    }

    public void setCronExp(String cronExp) {
        this.cronExp = cronExp;
    }

    public String getOperateUname() {
        return operateUname;
    }

    public void setOperateUname(String operateUname) {
        this.operateUname = operateUname;
    }

    public List<Record> getRedoList() {
        return redoList;
    }

    public void setRedoList(List<Record> redoList) {
        this.redoList = redoList;
    }

    public String getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(String queryDate) {
        if (CommonUtils.notEmpty(queryDate)) {
            this.queryDate = queryDate;
        }
    }
}
