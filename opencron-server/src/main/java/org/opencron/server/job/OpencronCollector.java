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

import org.opencron.server.service.ExecuteService;
import org.opencron.server.service.JobService;
import it.sauronsoftware.cron4j.*;
import org.opencron.common.job.Opencron;
import org.opencron.common.utils.CommonUtils;
import org.opencron.server.vo.JobVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by benjobs on 16/3/28.
 */
@Component
public class OpencronCollector implements TaskCollector {

    @Autowired
    private JobService jobService;

    @Autowired
    private ExecuteService executeService;

    private TaskTable taskTable;

    private Map<Long,Integer> jobIndex = new ConcurrentHashMap<Long, Integer>(0);

    /**
     * 初始化crontab任务,记录每个任务的索引...
     * @return
     */
    @Override
    public synchronized TaskTable getTasks() {
        if ( taskTable==null ) {
            taskTable = new TaskTable();
            List<JobVo> jobs = jobService.getCrontabJob();
            for (int index=0;index<jobs.size();index++) {
                final JobVo job = jobs.get(index);
                jobIndex.put(job.getJobId(),index);
                taskTable.add(new SchedulingPattern(job.getCronExp()),new Task() {
                    @Override
                    public void execute(TaskExecutionContext context) throws RuntimeException {
                        //自动执行
                        job.setExecType(Opencron.ExecType.AUTO.getStatus());
                        executeService.executeJob(job);
                    }
                });
            }
        }
        return taskTable;
    }

    /**
     * 将当前的job加入到crontab定时计划,并且加入索引值
     * @param job
     */
    public synchronized void addTask(final JobVo job) {
        jobIndex.put(job.getJobId(),jobIndex.size());
        taskTable.add(new SchedulingPattern(job.getCronExp()),new Task() {
            @Override
            public void execute(TaskExecutionContext context) throws RuntimeException {
                //自动执行
                job.setExecType(Opencron.ExecType.AUTO.getStatus());
                executeService.executeJob(job);
            }
        });
    }

    public synchronized void removeTask(Long jobId) {
        if (CommonUtils.notEmpty(jobId,jobIndex.get(jobId))) {
            taskTable.remove(jobIndex.get(jobId));
            Integer index = jobIndex.remove(jobId);
            for(Map.Entry<Long,Integer> entry:jobIndex.entrySet()){
                Long key = entry.getKey();
                Integer value = entry.getValue();
                /**
                 * 当前位置的索引已经被删除,后面的自动往前移一位...
                 */
                if (value > index ) {
                    jobIndex.put(key,value-1);
                }
            }
        }
    }

}
