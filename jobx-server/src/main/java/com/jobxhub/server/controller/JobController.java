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

package com.jobxhub.server.controller;

import java.util.*;

import com.jobxhub.common.Constants;
import com.jobxhub.common.util.DigestUtils;
import com.jobxhub.common.util.StringUtils;
import com.jobxhub.common.util.collection.ParamsMap;
import com.jobxhub.server.annotation.RequestRepeat;
import com.jobxhub.server.domain.JobBean;
import com.jobxhub.server.dto.User;
import com.jobxhub.server.support.JobXTools;
import com.jobxhub.server.service.*;
import com.jobxhub.server.tag.PageBean;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.server.dto.Agent;
import com.jobxhub.server.dto.Job;
import com.jobxhub.server.dto.Status;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.jobxhub.common.util.CommonUtils.notEmpty;
import static com.jobxhub.common.util.WebUtils.*;

@Controller
@RequestMapping("job")
public class JobController extends BaseController {

    @Autowired
    private ExecuteService executeService;

    @Autowired
    private JobService jobService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private UserService userService;

    @RequestMapping("view.htm")
    public String view(HttpSession session, HttpServletRequest request, PageBean pageBean, Job job, Model model) {
        jobService.getPageBean(session, pageBean, job);
        model.addAttribute("job", job);
        model.addAttribute("agents", agentService.getOwnerAgents(session));
        model.addAttribute("jobs", jobService.getAll());
        if (request.getParameter("refresh") != null) {
            return "/job/refresh";
        }
        return "/job/view";
    }

    /**
     * 同一台执行器上不能有重复名字的job
     *
     * @param jobId
     * @param agentId
     * @param name
     */
    @RequestMapping(value = "checkname.do", method = RequestMethod.POST)
    @ResponseBody
    public Status checkName(Long jobId, Long agentId, String name) {
        return Status.create(!jobService.existsName(jobId, agentId, name));
    }

    @RequestMapping(value = "checkdel.do", method = RequestMethod.POST)
    @ResponseBody
    public Status checkDelete(Long id) {
        boolean status = jobService.checkDelete(id);
        return Status.create(status);
    }

    @RequestMapping(value = "delete.do", method = RequestMethod.POST)
    @ResponseBody
    @RequestRepeat
    public Status delete(Long id) {
        try {
            jobService.delete(id);
            return Status.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.FALSE;
        }
    }

    @RequestMapping("add.htm")
    public String add(HttpSession session, Model model, Long id) {
        if (notEmpty(id)) {
            Agent agent = agentService.getAgent(id);
            model.addAttribute("agent", agent);
        }
        List<Agent> agents = agentService.getOwnerAgents(session);
        model.addAttribute("agents", agents);
        model.addAttribute("execUser", userService.getExecUser(JobXTools.getUserId(session)));
        return "/job/add";
    }

    @RequestMapping("addflow.htm")
    public String addflow(HttpSession session, Model model, Long id) {
        if (notEmpty(id)) {
            Agent agent = agentService.getAgent(id);
            model.addAttribute("agent", agent);
        }
        List<Agent> agents = agentService.getOwnerAgents(session);
        model.addAttribute("agents", agents);
        return "/job/addflow";
    }

    @RequestMapping(value = "search.do", method = RequestMethod.POST)
    @ResponseBody
    public PageBean<Job> search(HttpSession session, Long agentId, String jobName, Integer pageNo) {
        PageBean pageBean = new PageBean<JobBean>(6);
        pageBean.setPageNo(pageNo == null ? 1 : pageNo);
        if (agentId == null && CommonUtils.isEmpty(jobName)) {
            return pageBean;
        }
        return jobService.search(session, pageBean, agentId, jobName);
    }

    @RequestMapping(value = "save.do", method = RequestMethod.POST)
    @RequestRepeat(view = true)
    public String save(HttpSession session, Job jobParam, HttpServletRequest request) throws Exception {
        jobParam.setCommand(DigestUtils.passBase64(jobParam.getCommand()));
        if (jobParam.getJobId() != null) {
            Job job = jobService.getById(jobParam.getJobId());

            if (!jobService.checkJobOwner(session, job.getUserId()))
                return "redirect:/job/view.htm";
            /**
             * 将数据库中持久化的作业和当前修改的合并,当前修改的属性覆盖持久化的属性...
             */
            BeanUtils.copyProperties(
                    job,
                    jobParam,
                    "jobName",
                    "cronExp",
                    "command",
                    "comment",
                    "successExit",
                    "redo",
                    "runCount",
                    "jobType",
                    "runModel",
                    "warning",
                    "mobile",
                    "email",
                    "timeout",
                    "execUser"
            );
        }

        //单任务
        if (Constants.JobType.SIMPLE.getCode().equals(jobParam.getJobType())) {
            jobParam.setUserId(JobXTools.getUserId(session));
            jobParam.setCreateType(Constants.CreateType.NORMAL.getValue());
            jobParam.setToken(CommonUtils.uuid());
            Agent agent = agentService.getAgent(jobParam.getAgentId());
            if (agent != null) {
                if (agent.getPlatform() == null || agent.getPlatform() != 1) {
                    jobParam.setExecUser(null);
                }
            }
            jobParam.setPause(false);
            jobService.merge(jobParam);
        } else { //流程任务
            Map<String, String[]> map = request.getParameterMap();
            Object[] jobName = map.get("child.jobName");
            Object[] jobId = map.get("child.jobId");
            Object[] agentId = map.get("child.agentId");
            Object[] command = map.get("child.command");
            Object[] redo = map.get("child.redo");
            Object[] runCount = map.get("child.runCount");
            Object[] timeout = map.get("child.timeout");
            Object[] comment = map.get("child.comment");
            Object[] successExit = map.get("child.successExit");
            Object[] sn = map.get("child.sn");
            List<Job> children = new ArrayList<Job>(0);
            for (int i = 0; i < jobName.length; i++) {
                Job child = new Job();
                if (CommonUtils.notEmpty(jobId[i])) {
                    //子任务修改的..
                    Long jobid = Long.parseLong((String) jobId[i]);
                    child = jobService.getById(jobid);
                }
                child.setSn((String) sn[i]);
                child.setCreateType(Constants.CreateType.FLOW.getValue());
                child.setJobName(StringUtils.htmlEncode((String) jobName[i]));
                child.setAgentId(Long.parseLong((String) agentId[i]));
                child.setCommand(DigestUtils.passBase64((String) command[i]));
                child.setJobType(Constants.JobType.FLOW.getCode());
                child.setComment(StringUtils.htmlEncode((String) comment[i]));
                child.setSuccessExit(StringUtils.htmlEncode((String) successExit[i]));
                child.setTimeout(Integer.parseInt((String) timeout[i]));
                child.setRedo(Integer.parseInt((String) redo[i]));
                if (child.getRedo() == 0) {
                    child.setRunCount(null);
                } else {
                    child.setRunCount(Integer.parseInt((String) runCount[i]));
                }
                children.add(child);
            }

            //流程任务必须有子任务,没有的话不保存
            if (CommonUtils.isEmpty(children)) {
                return "redirect:/job/view.htm";
            }

            if (jobParam.getUserId() == null) {
                jobParam.setUserId(JobXTools.getUserId(session));
            }

            jobService.saveFlowJob(jobParam, children);
        }

        schedulerService.syncTigger(jobParam.getJobId());

        return "redirect:/job/view.htm";
    }

    @RequestMapping("editsingle.do")
    @ResponseBody
    @RequestRepeat
    public Job editSingleJob(HttpSession session, HttpServletResponse response, Long id) {
        Job job = jobService.getById(id);
        if (job == null) {
            write404(response);
            return null;
        }
        if (!jobService.checkJobOwner(session, job.getUserId())) return null;
        return job;
    }

    @RequestMapping("editflow.htm")
    public String editFlowJob(HttpSession session, Model model, Long id) {
        Job job = jobService.getById(id);
        if (job == null) {
            return "/error/404";
        }
        if (!jobService.checkJobOwner(session, job.getUserId()))
            return "redirect:/job/view.htm";
        model.addAttribute("job", job);
        List<Agent> agents = agentService.getOwnerAgents(session);
        model.addAttribute("agents", agents);
        return "/job/edit";
    }

    @RequestMapping(value = "edit.do", method = RequestMethod.POST)
    @ResponseBody
    @RequestRepeat(view = true)
    public Status edit(HttpSession session, Job job) throws Exception {
        Job dbJob = jobService.getById(job.getJobId());
        if (!jobService.checkJobOwner(session, dbJob.getUserId())) return Status.FALSE;
        dbJob.setCronExp(job.getCronExp());
        dbJob.setCommand(DigestUtils.passBase64(job.getCommand()));
        dbJob.setJobName(job.getJobName());
        dbJob.setSuccessExit(job.getSuccessExit());
        dbJob.setRedo(job.getRedo());
        dbJob.setRunCount(job.getRunCount());
        dbJob.setWarning(job.getWarning());
        dbJob.setTimeout(job.getTimeout());
        if (dbJob.getWarning()) {
            dbJob.setMobile(job.getMobile());
            dbJob.setEmail(job.getEmail());
        }
        dbJob.setComment(job.getComment());
        jobService.merge(dbJob);
        if (!dbJob.getPause()) {
            schedulerService.syncTigger(dbJob.getJobId());
        }
        return Status.TRUE;
    }

    @RequestMapping(value = "editcmd.do", method = RequestMethod.POST)
    @ResponseBody
    @RequestRepeat
    public Status editCmd(HttpSession session, Long jobId, String command) throws Exception {
        command = DigestUtils.passBase64(command);
        Job dbJob = jobService.getById(jobId);
        if (!jobService.checkJobOwner(session, dbJob.getUserId())) return Status.FALSE;
        dbJob.setCommand(command);
        jobService.merge(dbJob);
        schedulerService.syncTigger(dbJob.getJobId());
        return Status.TRUE;
    }

    /**
     * 检测当前的job是否正在运行中,运行中true,未运行false
     *
     * @param jobId
     * @return
     */
    @RequestMapping(value = "running.do", method = RequestMethod.POST)
    @ResponseBody
    public Status jobIsRunning(Long jobId) {
        return Status.create(recordService.isRunning(jobId));
    }

    @RequestMapping(value = "execute.do", method = RequestMethod.POST)
    @ResponseBody
    public Status remoteExecute(HttpSession session, Long id, String param) {
        final Job job = jobService.getById(id);//找到要执行的任务
        if (!jobService.checkJobOwner(session, job.getUserId())) return Status.FALSE;
        //手动执行
        Long userId = JobXTools.getUserId(session);
        if (StringUtils.isNotEmpty(param)) {
            job.setInputParam(param);
        }
        job.setUserId(userId);
        job.setAgent(agentService.getAgent(job.getAgentId()));
        //无等待返回前台响应.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    executeService.executeJob(job, Constants.ExecType.OPERATOR);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return Status.TRUE;
    }

    @RequestMapping("goexec.htm")
    public String goExec(HttpSession session, Model model) {
        model.addAttribute("agents", agentService.getOwnerAgents(session));
        return "/job/exec";
    }

    @RequestMapping(value = "pause.do", method = RequestMethod.POST)
    @ResponseBody
    public Status pause(Job job) {
        return Status.create(jobService.pauseJob(job));
    }

    /**
     * 更新任务的api调用认证token
     *
     * @param jobId
     * @return
     */
    @RequestMapping(value = "token.do", method = RequestMethod.POST)
    @ResponseBody
    public ParamsMap token(Long jobId) {
        Job job = jobService.getById(jobId);
        String token = CommonUtils.uuid();
        if (job != null) {
            job.setToken(CommonUtils.uuid());
            jobService.updateToken(jobId, token);
        }
        return ParamsMap.map().set("token", token);
    }

    @RequestMapping(value = "batchexec.do", method = RequestMethod.POST)
    @ResponseBody
    public Status batchExec(HttpSession session, String command, String agentIds) {
        if (notEmpty(agentIds) && notEmpty(command)) {
            command = DigestUtils.passBase64(command);
            Long userId = JobXTools.getUserId(session);
            try {
                this.executeService.executeBatchJob(userId, command, agentIds);
            } catch (Exception e) {
                e.printStackTrace();
                return Status.FALSE;
            }
        }
        return Status.TRUE;
    }

    @RequestMapping("detail/{id}.htm")
    public String showDetail(HttpSession session, Model model, @PathVariable("id") Long id) {
        Job job = jobService.getById(id);
        if (job == null) {
            return "/error/404";
        }
        if (!jobService.checkJobOwner(session, job.getUserId())) {
            return "redirect:/job/view.htm";
        }
        model.addAttribute("job", job);
        return "/job/detail";
    }

}
