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


package org.opencron.server.service;

import org.opencron.common.job.Opencron;
import org.opencron.server.job.OpencronCollector;
import org.opencron.server.vo.JobVo;
import org.quartz.*;
import org.quartz.Job;
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
    private AgentService agentService;

    @Autowired
    private OpencronCollector opencronCollector;

    private Scheduler quartzScheduler;

    private it.sauronsoftware.cron4j.Scheduler crontabScheduler;

    public SchedulerService() throws SchedulerException {
        this.quartzScheduler = new StdSchedulerFactory().getScheduler();
    }

    public boolean exists(Serializable jobId) throws SchedulerException {
        return quartzScheduler.checkExists(JobKey.jobKey(jobId.toString()));
    }

    public void put(List<JobVo> jobs, Job jobBean) throws SchedulerException {
        for(JobVo jobVo:jobs){
            put(jobVo,jobBean);
        }
    }

    public void put(JobVo job, Job jobBean) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobId().toString());
        CronTrigger cronTrigger = newTrigger().withIdentity(triggerKey).withSchedule(cronSchedule(job.getCronExp())).build();

        //when exists then delete..
        if (exists(job.getJobId())) {
            this.remove(job.getJobId());
        }
        //add new job 。。。
        JobDetail jobDetail = JobBuilder.newJob(jobBean.getClass()).withIdentity(JobKey.jobKey(job.getJobId().toString())).build();
        jobDetail.getJobDataMap().put(job.getJobId().toString(), job);
        jobDetail.getJobDataMap().put("jobBean", jobBean);
        Date date = quartzScheduler.scheduleJob(jobDetail, cronTrigger);
        logger.info("opencron: add success,cronTrigger:{}", cronTrigger, date);
    }

    public void remove(Serializable jobId) throws SchedulerException {
        if (exists(jobId)) {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobId.toString());
            quartzScheduler.pauseTrigger(triggerKey);// 停止触发器
            quartzScheduler.unscheduleJob(triggerKey);// 移除触发器
            quartzScheduler.deleteJob(JobKey.jobKey(jobId.toString()));// 删除任务
            logger.info("opencron: removed, triggerKey:{},", triggerKey);
        }
    }

    public void startQuartz() throws SchedulerException {
        if (quartzScheduler!=null && !quartzScheduler.isStarted()) {
            quartzScheduler.start();
        }
    }

    public void shutdown() throws SchedulerException {
        if (quartzScheduler!=null && !quartzScheduler.isShutdown()) {
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
        } else {
            //skip.....
        }
    }

    public void initCrontab() {
        if (this.crontabScheduler == null) {
            this.crontabScheduler = new it.sauronsoftware.cron4j.Scheduler();
            crontabScheduler.addTaskCollector(opencronCollector);
        } else {
            this.crontabScheduler.stop();
        }
        this.crontabScheduler.start();
    }


    public void syncJobTigger(Long jobId,ExecuteService executeService) throws SchedulerException {
        JobVo job = jobService.getJobVoById(jobId);
        job.setAgent(agentService.getAgent(job.getAgentId()));

        /**
         * 从crontab或者quartz里删除任务
         */
        opencronCollector.removeTask(job.getJobId());
        remove(job.getJobId());

        //自动执行
        if ( Opencron.ExecType.AUTO.getStatus().equals(job.getExecType()) ){
            if (Opencron.CronType.QUARTZ.getType().equals(job.getCronType())){
                /**
                 * 将作业加到quartz任务计划
                 */
                put(job, executeService);
            }else {
                /**
                 * 将作业加到crontab任务计划
                 */
                opencronCollector.addTask(job);
            }
        }
    }

    public void initQuartz(Job jobExecutor) throws SchedulerException {
        //quartz job
        logger.info("[opencron] init quartzJob...");
        List<JobVo> jobs = jobService.getJobVo(Opencron.ExecType.AUTO, Opencron.CronType.QUARTZ);
        for (JobVo job : jobs) {
            try {
                put(job,jobExecutor);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
        startQuartz();
    }
}