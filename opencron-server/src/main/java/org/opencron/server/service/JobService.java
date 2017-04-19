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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.opencron.common.job.Opencron;
import org.opencron.server.dao.QueryDao;
import org.opencron.server.domain.Job;
import org.opencron.server.domain.User;
import org.opencron.server.domain.Agent;
import org.opencron.server.job.OpencronTools;
import org.opencron.server.tag.PageBean;

import static org.opencron.common.job.Opencron.*;

import org.opencron.common.utils.CommonUtils;
import org.opencron.server.vo.JobVo;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

import static org.opencron.common.utils.CommonUtils.notEmpty;

@Service
public class JobService {

    @Autowired
    private QueryDao queryDao;

    @Autowired
    private AgentService agentService;

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(JobService.class);

    public Job getJob(Long jobId) {
        return queryDao.get(Job.class,jobId);
    }

    /**
     * 获取将要执行的任务
     * @return
     */
    public List<JobVo> getJobVo(ExecType execType, CronType cronType) {
        String sql = "SELECT T.*,D.name AS agentName,D.port,D.ip,D.password FROM T_JOB T LEFT JOIN T_AGENT D ON T.agentId = D.agentId WHERE IFNULL(T.flowNum,0)=0 AND cronType=? AND execType = ? AND T.status=1";
        List<JobVo> jobs = queryDao.sqlQuery(JobVo.class, sql, cronType.getType(), execType.getStatus());
        queryJobMore(jobs);
        return jobs;
    }

    public List<JobVo> getJobVoByAgentId(Agent agent, ExecType execType, CronType cronType) {
        String sql = "SELECT T.*,D.name AS agentName,D.port,D.ip,D.password FROM T_JOB T INNER JOIN T_AGENT D ON T.agentId = D.agentId WHERE IFNULL(T.flowNum,0)=0 AND cronType=? AND execType = ? AND T.status=1 AND D.agentId=? ";
        List<JobVo> jobs = queryDao.sqlQuery(JobVo.class, sql, cronType.getType(), execType.getStatus(),agent.getAgentId());
        queryJobMore(jobs);
        return jobs;
    }

    private void queryJobMore(List<JobVo> jobs) {
        if (CommonUtils.notEmpty(jobs)) {
            for (JobVo job : jobs) {
                job.setAgent(agentService.getAgent(job.getAgentId()));
                queryChildren(job);
                queryJobUser(job);
            }
        }
    }

    public List<Job> getJobsByJobType(HttpSession session,JobType jobType){
        String sql = "SELECT * FROM T_JOB WHERE status=1 AND jobType=?";
        if (JobType.FLOW.equals(jobType)) {
            sql +=" AND flowNum=0";
        }
        if (!OpencronTools.isPermission(session)) {
            User user = OpencronTools.getUser(session);
            sql += " AND userId = " + user.getUserId() + " AND agentId IN ("+user.getAgentIds()+")";
        }
        return queryDao.sqlQuery(Job.class,sql,jobType.getCode());
    }

    public List<JobVo> getCrontabJob() {
        logger.info("[opencron] init quartzJob...");
        return getJobVo(Opencron.ExecType.AUTO, Opencron.CronType.CRONTAB);
    }

    private void flushOpencron(){
        OpencronTools.CACHE.put(OpencronTools.CACHED_CRONTAB_JOB,getJobVo(Opencron.ExecType.AUTO, Opencron.CronType.CRONTAB));
    }

    public PageBean<JobVo> getJobVos(HttpSession session,PageBean pageBean, JobVo job) {
        String sql = "SELECT T.*,D.name AS agentName,D.port,D.ip,D.password,U.userName AS operateUname " +
                " FROM T_JOB AS T LEFT JOIN T_AGENT AS D ON T.agentId = D.agentId LEFT JOIN T_USER AS U ON T.userId = U.userId WHERE IFNULL(flowNum,0)=0 AND T.status=1 ";
        if (job != null) {
            if (notEmpty(job.getAgentId())) {
                sql += " AND T.agentId=" + job.getAgentId();
            }
            if (notEmpty(job.getCronType())) {
                sql += " AND T.cronType=" + job.getCronType();
            }
            if (notEmpty(job.getJobType())) {
                sql += " AND T.jobType=" + job.getJobType();
            }
            if (notEmpty(job.getExecType())) {
                sql += " AND T.execType=" + job.getExecType();
            }
            if (notEmpty(job.getRedo())) {
                sql += " AND T.redo=" + job.getRedo();
            }
            if (!OpencronTools.isPermission(session)) {
                User user = OpencronTools.getUser(session);
                sql += " AND T.userId = " + user.getUserId() + " AND T.agentId IN ("+user.getAgentIds()+")";
            }
        }
        pageBean = queryDao.getPageBySql(pageBean, JobVo.class, sql);
        List<JobVo> parentJobs = pageBean.getResult();

        for (JobVo parentJob : parentJobs) {
            queryChildren(parentJob);
        }
        pageBean.setResult(parentJobs);
        return pageBean;
    }

    private List<JobVo> queryChildren(JobVo job) {
        if (job.getJobType().equals(JobType.FLOW.getCode())) {
            String sql = "SELECT T.*,D.name AS agentName,D.port,D.ip,D.password,U.userName AS operateUname" +
                    " FROM T_JOB AS T LEFT JOIN T_AGENT AS D ON T.agentId = D.agentId LEFT JOIN T_USER AS U " +
                    " ON T.userId = U.userId WHERE T.status=1 AND T.flowId = ? AND T.flowNum>0 ORDER BY T.flowNum ASC";
            List<JobVo> childJobs = queryDao.sqlQuery(JobVo.class, sql, job.getFlowId());
            if (CommonUtils.notEmpty(childJobs)) {
                for(JobVo jobVo:childJobs){
                    jobVo.setAgent(agentService.getAgent(jobVo.getAgentId()));
                }
            }
            job.setChildren(childJobs);
            return childJobs;
        }
        return Collections.emptyList();
    }

    public Job addOrUpdate(Job job) {
        Job saveJob = (Job)queryDao.save(job);
        flushOpencron();
        return saveJob;
    }

    public JobVo getJobVoById(Long id) {
        String sql = "SELECT T.*,D.name AS agentName,D.port,D.ip,D.password,U.username AS operateUname " +
                " FROM T_JOB AS T LEFT JOIN T_AGENT AS D ON T.agentId = D.agentId LEFT JOIN T_USER AS U ON T.userId = U.userId WHERE T.jobId =?";
        JobVo job = queryDao.sqlUniqueQuery(JobVo.class, sql, id);
        queryJobMore(Arrays.asList(job));
        return job;
    }

    private void queryJobUser(JobVo job) {
        if (job!=null && job.getUserId()!=null) {
            User user = userService.getUserById(job.getUserId());
            job.setUser(user);
        }
    }

    public List<Job> getAll() {
        return queryDao.getAll(Job.class);
    }

    public List<JobVo> getJobByAgentId(Long agentId) {
        String sql = "SELECT T.*,D.name AS agentName,D.port,D.ip,D.password,U.userName AS operateUname " +
                " FROM T_JOB T LEFT JOIN T_USER U ON T.userId = U.userId LEFT JOIN T_AGENT D ON T.agentId = D.agentId WHERE T.agentId =?";
        return queryDao.sqlQuery(JobVo.class, sql, agentId);
    }

    public String checkName(Long jobId,Long agentId, String name) {
        String sql = "SELECT COUNT(1) FROM T_JOB WHERE agentId=? AND status=1 AND jobName=? ";
        if (notEmpty(jobId)) {
            sql += " AND jobId != " + jobId + " AND flowId != " + jobId;
        }
        return (queryDao.getCountBySql(sql, agentId,name)) > 0L ? "no" : "yes";
    }

    @Transactional(readOnly = false)
    public int delete(Long jobId) {
        int count = queryDao.createSQLQuery("UPDATE T_JOB SET status=0 WHERE jobId = " + jobId).executeUpdate();
        flushOpencron();
        return count;
    }

    @Transactional(readOnly = false)
    public void saveFlowJob(Job job, List<Job> children) throws SchedulerException {
        job.setLastChild(false);
        job.setUpdateTime(new Date());
        job.setFlowNum(0);//顶层sort是0
        /**
         * 保存最顶层的父级任务
         */
        if (job.getJobId()!=null) {
            addOrUpdate(job);
            /**
             * 当前作业已有的子作业
             */
            JobVo jobVo = new JobVo();
            jobVo.setJobType(JobType.FLOW.getCode());
            jobVo.setFlowId(job.getFlowId());

            /**
             * 取差集..
             */
            List<JobVo> hasChildren = queryChildren(jobVo);
            //数据库里已经存在的子集合..
            top:for(JobVo hasChild:hasChildren) {
                //当前页面提交过来的子集合...
                for(Job child:children){
                    if ( child.getJobId()!=null && child.getJobId().equals(hasChild.getJobId()) ) {
                        continue top;
                    }
                }
                /**
                 * 已有的子作业被删除的,则做删除操作...
                 */
                delete(hasChild.getJobId());
            }
        }else {
            Job job1 = addOrUpdate(job);
            job1.setFlowId(job1.getJobId());//flowId
            addOrUpdate(job1);
            job.setJobId(job1.getJobId());
        }

        for (int i=0;i<children.size();i++) {
            Job child = children.get(i);
            /**
             * 子作业的流程编号都为顶层父任务的jobId
             */
            child.setFlowId(job.getJobId());
            child.setUserId(job.getUserId());
            child.setExecType(job.getExecType());
            child.setUpdateTime(new Date());
            child.setJobType(JobType.FLOW.getCode());
            child.setFlowNum(i+1);
            child.setLastChild(child.getFlowNum()==children.size());
            child.setWarning(job.getWarning());
            child.setMobiles(job.getMobiles());
            child.setEmailAddress(job.getEmailAddress());
            addOrUpdate(child);
        }
    }

    public boolean checkJobOwner(HttpSession session,Long userId) {
        return OpencronTools.isPermission(session) || userId.equals(OpencronTools.getUserId(session));
    }

}
