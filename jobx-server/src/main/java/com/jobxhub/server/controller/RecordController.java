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

import com.jobxhub.server.dto.Record;
import com.jobxhub.server.dto.Status;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;

import com.jobxhub.common.Constants;
import com.jobxhub.server.service.*;
import com.jobxhub.server.tag.PageBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.jobxhub.common.util.CommonUtils.notEmpty;

@Controller
@RequestMapping("record")
public class RecordController {

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
     * @param record
     * @param model
     * @return
     */
    @RequestMapping("done.htm")
    public String queryDone(HttpSession session, PageBean pageBean, Record record,Model model) {
        model.addAttribute("agents", agentService.getOwnerAgents(session));
        if (notEmpty(record.getAgentId())) {
            model.addAttribute("jobs", jobService.getByAgent(record.getAgentId()));
        } else {
            model.addAttribute("jobs", jobService.getAll());
        }
        recordService.getPageBean(session, pageBean, record, true);
        return "/record/done";
    }

    @RequestMapping("running.htm")
    public String queryRunning(HttpSession session, PageBean pageBean, Record record, Model model, Boolean refresh) {

        model.addAttribute("agents", agentService.getOwnerAgents(session));

        if (notEmpty(record.getAgentId())) {
            model.addAttribute("jobs", jobService.getByAgent(record.getAgentId()));
        } else {
            model.addAttribute("jobs", jobService.getAll());
        }
        recordService.getPageBean(session, pageBean, record, false);
        return refresh == null ? "/record/running" : "/record/refresh";
    }

    @RequestMapping("refresh.htm")
    public String refresh(HttpSession session, PageBean pageBean, Record record, Model model) {
        return this.queryRunning(session, pageBean, record, model, true);
    }

    @RequestMapping("detail/{id}.htm")
    public String showDetail(Model model, @PathVariable("id") Long id) {
        Record record = recordService.getById(id);
        if (record == null) {
            return "/error/404";
        }
        model.addAttribute("record", record);
        return "/record/detail";
    }

    @RequestMapping(value = "kill.do", method = RequestMethod.POST)
    @ResponseBody
    public Status kill(HttpSession session, Long recordId) {
        Record record = recordService.getById(recordId);
        if (Constants.RunStatus.RERUNNING.getStatus().equals(record.getStatus())) {
            //父记录临时改为停止中
            record.setStatus(Constants.RunStatus.STOPPING.getStatus());
            recordService.merge(record);
            //得到当前正在重跑的子记录
            record = recordService.getReRunningSubJob(recordId);
        }
        if (!jobService.checkJobOwner(session, record.getUserId())) return Status.FALSE;
        executeService.killJob(record);
        return Status.TRUE;
    }

}
