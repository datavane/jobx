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
import com.jobxhub.common.Constants;
import com.jobxhub.common.job.Response;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.HttpClientUtils;
import com.jobxhub.common.util.collection.HashMap;
import com.jobxhub.service.entity.JobEntity;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Job implements Serializable {

    private Long jobId;
    private Long agentId;
    private String agentName;
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
    private Integer jobType;
    private Integer createType;
    private Boolean warning;
    private String mobile;
    private Boolean pause;
    private String email;
    private String callbackURL;

    //job是否分布式部署
    private Boolean cluster = false;

    //job分布式在哪些机器上
    private List<Agent> clusterAgent;

    //运行超时的截止时间
    private Integer timeout;
    private String token;//api调用的认证token
    private String sn;
    private Agent agent;
    private User user;
    private String operateUname;


    private String inputParam;//手动触发时的参数

    private Integer alarmCode;//通知类型
    private Integer alarmType;//通知方式

    public Job() {

    }

    public static Function<JobEntity,Job> transferModel = new Function<JobEntity, Job>() {
        @Override
        public Job apply(JobEntity input) {
            Job job = new Job();
            BeanUtils.copyProperties(input,job);
            return job;
        }
    };

    public static Function<Job,JobEntity> transferEntity = new Function<Job,JobEntity>() {
        @Override
        public JobEntity apply(Job input) {
            JobEntity jobEntity = new JobEntity();
            BeanUtils.copyProperties(input,jobEntity);
            return jobEntity;
        }
    };

    public Job(Long userId, String command, Agent agent) {
        this.jobId = 0L;
        this.jobName = agent.getName() + "-batchJob";
        this.userId = userId;
        this.command = command;
        this.agent = agent;
        this.agentId = agent.getAgentId();
        this.redo = 0;
        this.runCount = 0;
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
        this.execUser = execUser;
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

    public String getCallbackURL() {
        return callbackURL;
    }

    public void setCallbackURL(String callbackURL) {
        this.callbackURL = callbackURL;
    }

    public Boolean getCluster() {
        return cluster;
    }

    public void setCluster(Boolean cluster) {
        this.cluster = cluster;
    }

    public List<Agent> getClusterAgent() {
        return clusterAgent;
    }

    public void setClusterAgent(List<Agent> clusterAgent) {
        this.clusterAgent = clusterAgent;
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

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOperateUname() {
        return operateUname;
    }

    public void setOperateUname(String operateUname) {
        this.operateUname = operateUname;
    }

    public String getInputParam() {
        return inputParam;
    }

    public void setInputParam(String inputParam) {
        this.inputParam = inputParam;
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

    public void callBack(Response response, Constants.ExecType execType) {
        if (execType.getStatus().equals(Constants.ExecType.API.getStatus())) {
            if (CommonUtils.notEmpty(this.getCallbackURL())) {
                try {
                    Map<String,Object> params = new HashMap<String, Object>(0);
                    params.put("jobId", this.getJobId());
                    params.put("startTime", response.getStartTime());
                    params.put("endTime", response.getEndTime());
                    params.put("success", response.isSuccess());
                    params.put("message", response.getMessage());
                    HttpClientUtils.httpPostRequest(this.getCallbackURL(), params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
