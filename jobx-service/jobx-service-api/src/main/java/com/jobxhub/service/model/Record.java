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
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.service.entity.RecordEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
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

    public void start(){
        setStartTime(new Date());
    }

    public void end(){
        setEndTime(new Date());
    }

    public void setQueryDate(String queryDate) {
        if (CommonUtils.notEmpty(queryDate)) {
            this.queryDate = queryDate;
        }
    }
}
