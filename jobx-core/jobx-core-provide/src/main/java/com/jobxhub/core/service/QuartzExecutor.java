package com.jobxhub.core.service;

import com.jobxhub.common.Constants;
import com.jobxhub.core.model.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class QuartzExecutor implements org.quartz.Job {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        String key = jobExecutionContext.getJobDetail().getKey().getName();
        Job job = (Job) jobExecutionContext.getJobDetail().getJobDataMap().get(key);
        try {
            ExecuteService executeService = (ExecuteService) jobExecutionContext.getJobDetail().getJobDataMap().get("executor");
            executeService.executeJob(job, Constants.ExecType.AUTO);
            logger.info("[JobX] job:{} at {}:{}", job, null);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
    }

}
