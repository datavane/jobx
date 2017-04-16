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

import org.opencron.common.job.Opencron;
import org.opencron.server.domain.User;
import org.opencron.server.domain.Agent;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class JobVo implements Serializable {
    private Long jobId;
    private String jobName;
    private Long agentId;
    private Integer cronType;
    private String cronExp;
    private String command;
    private Integer execType;
    private String comment;
    private Long userId;
    private Date updateTime;
    private Integer redo;
    private Integer runCount;
    private Integer runModel;

    /**
     * 0:单一任务
     * 1:流程任务
     */
    private Integer jobType;

    private Long flowId;

    private Integer flowNum;

    private Agent agent;

    private String agentName;

    private String password;

    private String operateUname;

    private String ip;

    private Integer port;

    private User user;

    //子任务
    private List<JobVo> children;

    //是否为流程任务的最后一个子任务
    private Boolean lastChild;

    private Boolean warning;

    private String mobiles;

    private String emailAddress;

    //运行超时的截止时间
    private Integer timeout;

    private String csrf;

    public JobVo(){}

    public JobVo(Long userId, String command, Agent agent) {
        this.jobId = 0L;
        this.jobName = agent.getName()+"-batchJob";
        this.userId = userId;
        this.command = command;
        this.execType = Opencron.ExecType.BATCH.getStatus();
        this.agent = agent;
        this.agentId = agent.getAgentId();
        this.ip = agent.getIp();
        this.port = agent.getPort();
        this.password = agent.getPassword();
        this.redo = 0;
        this.runCount = 0;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Integer getCronType() {
        return cronType;
    }

    public void setCronType(Integer cronType) {
        this.cronType = cronType;
    }

    public String getCronExp() {
        return cronExp;
    }

    public void setCronExp(String cronExp) {
        this.cronExp = cronExp;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Integer getExecType() {
        return execType;
    }

    public void setExecType(Integer execType) {
        this.execType = execType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public Integer getRunModel() {
        return runModel;
    }

    public void setRunModel(Integer runModel) {
        this.runModel = runModel;
    }

    public Integer getJobType() {
        return jobType;
    }

    public void setJobType(Integer jobType) {
        this.jobType = jobType;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Integer getFlowNum() {
        return flowNum;
    }

    public void setFlowNum(Integer flowNum) {
        this.flowNum = flowNum;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOperateUname() {
        return operateUname;
    }

    public void setOperateUname(String operateUname) {
        this.operateUname = operateUname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<JobVo> getChildren() {
        return children;
    }

    public void setChildren(List<JobVo> children) {
        this.children = children;
    }

    public Boolean getLastChild() {
        return lastChild;
    }

    public void setLastChild(Boolean lastChild) {
        this.lastChild = lastChild;
    }

    public Boolean getWarning() {
        return warning;
    }

    public void setWarning(Boolean warning) {
        this.warning = warning;
    }

    public String getMobiles() {
        return mobiles;
    }

    public void setMobiles(String mobiles) {
        this.mobiles = mobiles;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getCsrf() {
        return csrf;
    }

    public void setCsrf(String csrf) {
        this.csrf = csrf;
    }
}
