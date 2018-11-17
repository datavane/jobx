package com.jobxhub.service.service;

import com.jobxhub.common.Constants;
import com.jobxhub.service.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class QuartzExecutor implements org.quartz.Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        String key = jobExecutionContext.getJobDetail().getKey().getName();
        Job job = (Job) jobExecutionContext.getJobDetail().getJobDataMap().get(key);
        try {
            ExecuteService executeService = (ExecuteService) jobExecutionContext.getJobDetail().getJobDataMap().get("executor");
            executeService.executeJob(job, Constants.ExecType.AUTO);
            log.info("[JobX] job:{} at {}:{}", job, null);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

}
