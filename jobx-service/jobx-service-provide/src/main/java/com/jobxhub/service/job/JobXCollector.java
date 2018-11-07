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

package com.jobxhub.service.job;

import it.sauronsoftware.cron4j.*;
import com.jobxhub.common.Constants;
import com.jobxhub.service.service.ExecuteService;
import com.jobxhub.service.model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import com.jobxhub.common.util.collection.HashMap;

/**
 * Created by benjobs on 16/3/28.
 */
@Component
public class JobXCollector implements TaskCollector {

    @Autowired
    private ExecuteService executeService;

    private TaskTable taskTable;

    private Map<Long, Integer> jobIndex = new HashMap<Long, Integer>(0);

    @Override
    public synchronized TaskTable getTasks() {
        return taskTable = (taskTable == null ? new TaskTable() : taskTable);
    }

    public synchronized void add(final Job job) {
        if (job != null && !exists(job.getJobId())) {
            jobIndex.put(job.getJobId(), jobIndex.size());
            this.getTasks().add(new SchedulingPattern(job.getCronExp()), new Task() {
                @Override
                public void execute(TaskExecutionContext context) throws RuntimeException {
                    executeService.executeJob(job, Constants.ExecType.AUTO);
                }
            });
        }
    }

    public boolean exists(final Long jobId) {
        return jobIndex.containsKey(jobId);
    }

    public synchronized void remove(Long jobId) {
        if (jobId != null && jobIndex.get(jobId) != null) {
            this.getTasks().remove(jobIndex.get(jobId));
            Integer index = jobIndex.remove(jobId);
            for (Map.Entry<Long, Integer> entry : jobIndex.entrySet()) {
                Long key = entry.getKey();
                Integer value = entry.getValue();
                /**
                 * 当前位置的索引已经被删除,后面的自动往前移一位...
                 */
                if (value > index) {
                    jobIndex.put(key, value - 1);
                }
            }
        }
    }

}
