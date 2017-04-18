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

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;
import org.opencron.common.job.Opencron;
import org.opencron.server.job.OpencronTools;
import org.opencron.server.tag.PageBean;
import org.apache.commons.codec.digest.DigestUtils;
import org.opencron.common.utils.WebUtils;
import org.opencron.server.domain.Agent;
import org.opencron.server.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/agent")
public class AgentController extends BaseController{

    @Autowired
    private AgentService agentService;

    @RequestMapping("/view")
    public String queryAllAgent(HttpSession session,HttpServletRequest request, Model model, PageBean pageBean) {
        agentService.getOwnerAgent(session,pageBean);
        if (request.getParameter("refresh") != null) {
            return "/agent/refresh";
        }
        model.addAttribute("connAgents",agentService.getAgentByConnType(Opencron.ConnType.CONN));
        return "/agent/view";
    }

    @RequestMapping("/checkname")
    public void checkName(HttpServletResponse response, Long id, String name) {
        String result = agentService.checkName(id, name);
        WebUtils.writeHtml(response, result);
    }


    @RequestMapping("/checkhost")
    public void checkhost(HttpServletResponse response, Long id,String ip) {
        String result = agentService.checkhost(id, ip);
        WebUtils.writeHtml(response, result);
    }


    @RequestMapping("/addpage")
    public String addPage(Model model) {
        List<Agent> agentList = agentService.getAgentByConnType(Opencron.ConnType.CONN);
        model.addAttribute("connAgents",agentList);
        return "/agent/add";
    }

    @RequestMapping("/add")
    public String add(HttpSession session, Agent agent) {
        if (!agent.getWarning()) {
            agent.setMobiles(null);
            agent.setEmailAddress(null);
        }

        //直联
        if (Opencron.ConnType.CONN.getType().equals(agent.getProxy())) {
            agent.setProxyAgent(null);
        }

        agent.setPassword(DigestUtils.md5Hex(agent.getPassword()));
        agent.setStatus(true);
        agent.setUpdateTime(new Date());
        agentService.addOrUpdate(agent);
        return "redirect:/agent/view?csrf="+ OpencronTools.getCSRF(session);
    }

    @RequestMapping("/editpage")
    public void editPage(HttpServletResponse response,Long id) {
        Agent agent = agentService.getAgent(id);
        WebUtils.writeJson(response, JSON.toJSONString(agent));
    }

    @RequestMapping("/edit")
    public void edit(HttpServletResponse response, Agent agent) {
        Agent agent1 = agentService.getAgent(agent.getAgentId());
        agent1.setName(agent.getName());
        agent1.setProxy(agent.getProxy());
        if (Opencron.ConnType.CONN.getType().equals(agent.getProxy())) {
            agent1.setProxyAgent(null);
        }else {
            agent1.setProxyAgent(agent.getProxyAgent());
        }
        agent1.setPort(agent.getPort());
        agent1.setWarning(agent.getWarning());
        if (agent.getWarning()) {
            agent1.setMobiles(agent.getMobiles());
            agent1.setEmailAddress(agent.getEmailAddress());
        }
        agent1.setComment(agent.getComment());
        agent1.setUpdateTime(new Date());
        agentService.addOrUpdate(agent1);
        WebUtils.writeHtml(response, "success");
    }

    @RequestMapping("/pwdpage")
    public void pwdPage(HttpServletResponse response,Long id) {
        Agent agent = agentService.getAgent(id);
        WebUtils.writeJson(response, JSON.toJSONString(agent));
    }

    @RequestMapping("/editpwd")
    public void editPwd(HttpServletResponse response,Long id, String pwd0, String pwd1, String pwd2) {
        String result = agentService.editPwd(id, pwd0, pwd1, pwd2);
        WebUtils.writeHtml(response, result);
    }

    @RequestMapping("/detail")
    public String showDetail(Model model, Long id) {
        Agent agent = agentService.getAgent(id);
        model.addAttribute("agent", agent);
        return "/agent/detail";
    }

    @RequestMapping("/getConnAgents")
    public void getConnAgents(HttpServletResponse response) {
        List<Agent> agents = agentService.getAgentByConnType(Opencron.ConnType.CONN);
        WebUtils.writeJson(response, JSON.toJSONString(agents));
    }
}
