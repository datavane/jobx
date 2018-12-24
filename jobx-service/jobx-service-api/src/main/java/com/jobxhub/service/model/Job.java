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
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Map;

@Data
public class Job implements Serializable {

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
     * 告警通知方式...
     */
    private Integer[] alarmType;
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

    /**
     * 手动触发时的动态参数
     */
    private String runParam;

    public Job() {

    }

    public static Function<JobEntity, Job> transferModel = input -> {
        Job job = new Job();
        BeanUtils.copyProperties(input, job);
        return job;
    };

    public static Function<Job, JobEntity> transferEntity = input -> {
        JobEntity jobEntity = new JobEntity();
        BeanUtils.copyProperties(input, jobEntity);
        return jobEntity;
    };

    public Job(Long userId, String command, Agent agent) {
        this.jobId = 0L;
        this.jobName = agent.getAgentName() + "-batchJob";
        this.userId = userId;
        this.command = command;
        this.agent = agent;
        this.agentId = agent.getAgentId();
        this.runCount = 0;
    }

    public void callBack(Response response, Constants.ExecType execType) {
        if (execType.getStatus().equals(Constants.ExecType.API.getStatus())) {
            if (CommonUtils.notEmpty(this.getCallbackURL())) try {
                Map<String, Object> params = new HashMap<>(0);
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
