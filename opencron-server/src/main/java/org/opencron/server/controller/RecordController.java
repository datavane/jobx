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

package org.opencron.server.controller;

import javax.servlet.http.HttpSession;

import org.opencron.common.job.Opencron;
import org.opencron.server.domain.Record;
import org.opencron.server.service.*;
import org.opencron.server.tag.PageBean;
import org.opencron.server.vo.RecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.opencron.common.utils.CommonUtils.notEmpty;

@Controller
@RequestMapping("record")
public class RecordController extends BaseController {

    @Autowired
    private RecordService recordService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private JobService jobService;

    @Autowired
    private ExecuteService executeService;

    /**
     * 查询已完成任务列表
     *
     * @param pageBean
     * @param recordVo
     * @param model
     * @return
     */
     @RequestMapping("done.htm")
    public String queryDone(HttpSession session, PageBean pageBean, RecordVo recordVo, String queryTime, Model model) {

        model.addAttribute("agents", agentService.getOwnerAgents(session));

        if (notEmpty(recordVo.getSuccess())) {
            model.addAttribute("success", recordVo.getSuccess());
        }
        if (notEmpty(recordVo.getAgentId())) {
            model.addAttribute("agentId", recordVo.getAgentId());
        }

        if (notEmpty(recordVo.getAgentId())) {
            model.addAttribute("agentId", recordVo.getAgentId());
            model.addAttribute("jobs", jobService.getJobByAgentId(recordVo.getAgentId()));
        } else {
            model.addAttribute("jobs", jobService.getAll());
        }

        if (notEmpty(recordVo.getJobId())) {
            model.addAttribute("jobId", recordVo.getJobId());
        }
        if (notEmpty(queryTime)) {
            model.addAttribute("queryTime", queryTime);
        }
        if (notEmpty(recordVo.getExecType())) {
            model.addAttribute("execType", recordVo.getExecType());
        }
        recordService.query(session, pageBean, recordVo, queryTime, true);

        return "/record/done";
    }

     @RequestMapping("running.htm")
    public String queryRunning(HttpSession session, PageBean pageBean, RecordVo recordVo, String queryTime, Model model,Boolean refresh) {

        model.addAttribute("agents", agentService.getOwnerAgents(session));

        if (notEmpty(recordVo.getAgentId())) {
            model.addAttribute("agentId", recordVo.getAgentId());
            model.addAttribute("jobs", jobService.getJobByAgentId(recordVo.getAgentId()));
        } else {
            model.addAttribute("jobs", jobService.getAll());
        }

        if (notEmpty(recordVo.getJobId())) {
            model.addAttribute("jobId", recordVo.getJobId());
        }
        if (notEmpty(queryTime)) {
            model.addAttribute("queryTime", queryTime);
        }
        if (notEmpty(recordVo.getExecType())) {
            model.addAttribute("execType", recordVo.getExecType());
        }
        recordService.query(session, pageBean, recordVo, queryTime, false);
        return refresh==null?"/record/running":"/record/refresh";
    }

     @RequestMapping("refresh.htm")
    public String refresh(HttpSession session,PageBean pageBean, RecordVo recordVo, String queryTime, Model model) {
        return this.queryRunning(session,pageBean,recordVo,queryTime,model,true);
    }

     @RequestMapping("detail/{id}.htm")
    public String showDetail(Model model,@PathVariable("id") Long id) {
        RecordVo recordVo = recordService.getDetailById(id);
        if (recordVo == null) {
            return "/error/404";
        }
        model.addAttribute("record", recordVo);
        return "/record/detail";
    }

    @RequestMapping(value = "kill.do",method= RequestMethod.POST)
    @ResponseBody
    public boolean kill(HttpSession session, Long recordId) {
        Record record = recordService.get(recordId);
        if (Opencron.RunStatus.RERUNNING.getStatus().equals(record.getStatus())) {
            //父记录临时改为停止中
            record.setStatus(Opencron.RunStatus.STOPPING.getStatus());
            recordService.merge(record);
            //得到当前正在重跑的子记录
            record = recordService.getReRunningSubJob(recordId);
        }
        if (!jobService.checkJobOwner(session, record.getUserId())) return false;
        return executeService.killJob(record);
    }

}
