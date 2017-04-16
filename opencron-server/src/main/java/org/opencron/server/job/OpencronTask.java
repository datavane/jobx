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


package org.opencron.server.job;

import org.opencron.server.service.*;
import org.opencron.common.job.Opencron;
import org.opencron.common.utils.CommonUtils;
import org.opencron.server.domain.Record;
import org.opencron.server.domain.Agent;
import org.opencron.server.vo.JobVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class OpencronTask implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(OpencronTask.class);

    @Autowired
    private AgentService agentService;

    @Autowired
    private ExecuteService executeService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private JobService jobService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private OpencronMonitor opencronMonitor;

    @Override
    public void afterPropertiesSet() throws Exception {
        configService.initDataBase();
        //检测所有的agent...
        clearCache();
        //通知所有的agent,启动心跳检测...
        opencronMonitor.start();
        schedulerService.initQuartz(executeService);
        schedulerService.initCrontab();
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void reExecuteJob() {
        logger.info("[opencron] reExecuteIob running...");
        final List<Record> records = recordService.getReExecuteRecord();
        if (CommonUtils.notEmpty(records)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (final Record record : records) {
                        final JobVo jobVo = jobService.getJobVoById(record.getJobId());
                        logger.info("[opencron] reexecutejob:jobName:{},jobId:{},recordId:{}", jobVo.getJobName(), jobVo.getJobId(), record.getRecordId());
                        final Thread thread = new Thread(new Runnable() {
                            public void run() {
                                jobVo.setAgent(agentService.getAgent(jobVo.getAgentId()));
                                executeService.reExecuteJob(record, jobVo, Opencron.JobType.SINGLETON);
                            }
                        });
                        thread.start();
                    }
                }
            }).start();
        }
    }

    private void clearCache() {
        OpencronTools.CACHE.remove(OpencronTools.CACHED_AGENT_ID);
        OpencronTools.CACHE.remove(OpencronTools.CACHED_CRONTAB_JOB);
    }


}
