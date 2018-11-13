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


package com.jobxhub.service.service;

import com.jobxhub.service.api.JobService;
import com.jobxhub.service.job.JobXRegistry;
import com.jobxhub.service.model.Job;
import org.quartz.*;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


@Service
public final class SchedulerService {

    private final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    @Autowired
    private JobService jobService;

    @Autowired
    private JobXRegistry jobxRegistry;

    @Autowired
    private ExecuteService executeService;

    private Scheduler quartzScheduler;

    public boolean exists(Serializable jobId) throws SchedulerException {
        if (jobId == null || JobKey.jobKey(jobId.toString()) == null) {
            return false;
        }
        return quartzScheduler.checkExists(JobKey.jobKey(jobId.toString()));
    }

    public void put(List<Job> jobs) throws SchedulerException {
        for (Job job : jobs) {
            put(job);
        }
    }

    public void put(Job job) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobId().toString());
        CronTrigger cronTrigger = newTrigger().withIdentity(triggerKey).withSchedule(cronSchedule(job.getCronExp())).build();
        //when exists then delete..
        if (exists(job.getJobId())) {
            this.remove(job.getJobId());
        }
        //add new job 。。。
        JobDetail jobDetail = JobBuilder.newJob(QuartzExecutor.class).withIdentity(JobKey.jobKey(job.getJobId().toString())).build();
        jobDetail.getJobDataMap().put(job.getJobId().toString(), job);
        jobDetail.getJobDataMap().put("executor",executeService);
        Date date = quartzScheduler.scheduleJob(jobDetail, cronTrigger);
        if (logger.isInfoEnabled()) {
            logger.info("jobx: add success,cronTrigger:{}", cronTrigger, date);
        }
    }

    public void remove(Serializable jobId) throws SchedulerException {
        if (exists(jobId)) {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobId.toString());
            quartzScheduler.pauseTrigger(triggerKey);// 停止触发器
            quartzScheduler.unscheduleJob(triggerKey);// 移除触发器
            quartzScheduler.deleteJob(JobKey.jobKey(jobId.toString()));// 删除任务
            if (logger.isInfoEnabled()) {
                logger.info("jobx: removed, triggerKey:{},", triggerKey);
            }
        }
    }

    public void startQuartz() throws SchedulerException {
        if (quartzScheduler != null && !quartzScheduler.isStarted()) {
            quartzScheduler.start();
        }
    }

    public void shutdown() throws SchedulerException {
        if (quartzScheduler != null && !quartzScheduler.isShutdown()) {
            quartzScheduler.shutdown();
        }
    }

    public void pause(Serializable jobId) throws SchedulerException {
        if (exists(jobId)) {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobId.toString());
            quartzScheduler.pauseTrigger(triggerKey);
        }
    }

    public void resume(Serializable jobId) throws SchedulerException {
        if (exists(jobId)) {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobId.toString());
            quartzScheduler.resumeTrigger(triggerKey);
        }
    }

    public void syncTigger(Job job) throws Exception {
        //将该作业从zookeeper中移除掉....
        jobxRegistry.jobUnRegister(job.getJobId());
        jobxRegistry.jobRegister(job.getJobId());
    }

    public void syncTigger(Long jobId) throws Exception {
        Job job = jobService.getById(jobId);
        this.syncTigger(job);
    }

    public void initJob() throws SchedulerException {
        this.quartzScheduler = new StdSchedulerFactory().getScheduler();
        this.startQuartz();
    }


}