package com.jobxhub.service.api;

import com.jobxhub.common.Constants;
import com.jobxhub.service.model.Job;
import com.jobxhub.service.model.User;
import com.jobxhub.service.vo.PageBean;

import java.util.List;

public interface JobService {
    int getCountByType(Constants.JobType jobType);

    PageBean getPageBean(PageBean pageBean, Job job);

    List<Job> getByAgent(Long id);

    Job getById(Long jobId);

    List<Job> getScheduleJob();

    void addJob(Job job);

    void addNode(Job job);

    void addFlow(Job job);

    List<Job> getJob(Integer jobType);

}
