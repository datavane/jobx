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

package org.opencron.server.domain;

import org.opencron.common.job.Opencron;
import org.opencron.common.utils.CommonUtils;
import org.opencron.server.vo.JobVo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "T_RECORD")
public class Record implements Serializable {

    @Id
    @GeneratedValue
    private Long recordId;
    private Long jobId;
    private Long agentId;
    private Long userId;


    @Lob
    @Column(columnDefinition="TEXT")
    private String command;
    private Integer returnCode;
    private Integer success;
    private Date startTime;
    private Date endTime;
    private Integer execType;
    @Lob
    @Column(columnDefinition="TEXT")
    private String message;
    private Integer redoCount;
    private Integer status;
    private String pid;
    private Integer redo;
    private Integer runCount;

    /**
     * 重跑记录对象的父记录
     */
    private Long parentId;

    private Long groupId;

    /**
     * 流程任务的执行序号
     */
    private Integer flowNum;

    /**
     * 任务类型(0:单一任务,1:流程任务)
     */
    private Integer jobType;

    public Record() {
    }

    public Record(JobVo jobVo) {
        this.setJobId(jobVo.getJobId());
        this.setAgentId(jobVo.getAgentId());
        this.setUserId(jobVo.getUserId());
        this.setExecType( jobVo.getExecType() );
        this.setCommand(jobVo.getCommand());//执行的命令
        this.setStartTime(new Date());//开始执行的时间
        this.setRedo(jobVo.getRedo());//失败是否重新执行
        this.setRunCount(jobVo.getRunCount());//失败后重新执行次数
        this.setRedoCount(0);//运行次数
        this.setSuccess(Opencron.ResultStatus.SUCCESSFUL.getStatus());
        this.setStatus(Opencron.RunStatus.RUNNING.getStatus());//任务还未完成
        this.setPid(CommonUtils.uuid());
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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

    public Integer getFlowNum() {
        return flowNum;
    }

    public void setFlowNum(Integer flowNum) {
        this.flowNum = flowNum;
    }


    @Override
    public String toString() {
        return "Record{" +
                "recordId=" + recordId +
                ", jobId=" + jobId +
                ", agentId=" + agentId +
                ", userId=" + userId +
                ", command='" + command + '\'' +
                ", returnCode=" + returnCode +
                ", success=" + success +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", execType=" + execType +
                ", message='" + message + '\'' +
                ", redoCount=" + redoCount +
                ", status=" + status +
                ", pid='" + pid + '\'' +
                ", redo=" + redo +
                ", runCount=" + runCount +
                ", parentId=" + parentId +
                ", groupId=" + groupId +
                ", flowNum=" + flowNum +
                ", jobType=" + jobType +
                '}';
    }
}
