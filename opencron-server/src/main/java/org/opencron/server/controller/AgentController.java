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
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.opencron.common.job.Opencron;
import org.opencron.common.utils.CommonUtils;
import org.opencron.common.utils.HttpUtils;
import org.opencron.common.utils.PropertyPlaceholder;
import org.opencron.server.job.OpencronTools;
import org.opencron.server.service.ExecuteService;
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
public class AgentController extends BaseController {

    @Autowired
    private AgentService agentService;

    @Autowired
    private ExecuteService executeService;

    @RequestMapping("/view")
    public String queryAllAgent(HttpSession session, HttpServletRequest request, Model model, PageBean pageBean) {
        agentService.getOwnerAgent(session, pageBean);
        if (request.getParameter("refresh") != null) {
            return "/agent/refresh";
        }
        model.addAttribute("connAgents", agentService.getAgentByConnType(Opencron.ConnType.CONN));
        return "/agent/view";
    }

    @RequestMapping("/checkname")
    public void checkName(HttpServletResponse response, Long id, String name) {
        boolean exists = agentService.existsName(id, name);
        WebUtils.writeHtml(response, exists ? "false" : "true");
    }

    @RequestMapping("/checkDelete")
    public void checkDelete(HttpServletResponse response, Long id) {
        String result = agentService.checkDelete(id);
        WebUtils.writeHtml(response, result);
    }

    @RequestMapping("/delete")
    public void delete(HttpServletResponse response, Long id) {
        agentService.delete(id);
        WebUtils.writeHtml(response, "true");
    }

    @RequestMapping("/checkhost")
    public void checkhost(HttpServletResponse response, Long id, String ip) {
        boolean exists = agentService.existshost(id, ip);
        WebUtils.writeHtml(response, exists ? "false" : "true");
    }


    @RequestMapping("/addpage")
    public String addPage(Model model) {
        List<Agent> agentList = agentService.getAgentByConnType(Opencron.ConnType.CONN);
        model.addAttribute("connAgents", agentList);
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
        agent.setDeleted(false);
        agent.setUpdateTime(new Date());
        agentService.addOrUpdate(agent);
        return "redirect:/agent/view?csrf=" + OpencronTools.getCSRF(session);
    }

    @RequestMapping("/autoreg")
    public void autoReg(HttpServletRequest request, HttpServletResponse response, Agent agent, String key) {
        String ip = WebUtils.getIp(request);
        String format = "{status:%d,message:'%s'}";
        if (ip == null) {
            WebUtils.writeJson(response, String.format(format,500,"can't get agent'ip"));
            return;
        }

        //验证Key是否与服务器端一致
        String serverAutoRegKey = PropertyPlaceholder.get("opencron.autoRegKey");
        if (CommonUtils.notEmpty(serverAutoRegKey)) {
            if (CommonUtils.isEmpty(key) || !key.equals(serverAutoRegKey)) {
                WebUtils.writeJson(response, String.format(format,400,"auto register key error!"));
            }
        }

        Agent dbAgent = agentService.getAgentByMachineId(agent.getMachineId());
        //agent ip发生改变的情况下，自动重新注册
        if (dbAgent!=null) {
            dbAgent.setIp(ip);
            agentService.addOrUpdate(dbAgent);
            WebUtils.writeJson(response, String.format(format,200,ip));
        }else {
            //新的机器，需要自动注册.
            agent.setIp(ip);
            agent.setWarning(false);
            agent.setMobiles(null);
            agent.setEmailAddress(null);
            agent.setProxy(Opencron.ConnType.CONN.getType());
            agent.setProxyAgent(null);
            agent.setStatus(true);
            agent.setDeleted(false);
            agent.setUpdateTime(new Date());
            agentService.addOrUpdate(agent);
            WebUtils.writeJson(response, String.format(format,200,ip));
        }
    }

    @RequestMapping("/editpage")
    public void editPage(HttpServletResponse response, Long id) {
        Agent agent = agentService.getAgent(id);
        if (agent == null) {
            WebUtils.write404(response);
        }
        WebUtils.writeJson(response, JSON.toJSONString(agent));
    }

    @RequestMapping("/edit")
    public void edit(HttpServletResponse response, Agent agent) {
        Agent agent1 = agentService.getAgent(agent.getAgentId());
        agent1.setName(agent.getName());
        agent1.setProxy(agent.getProxy());
        if (Opencron.ConnType.CONN.getType().equals(agent.getProxy())) {
            agent1.setProxyAgent(null);
        } else {
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
        WebUtils.writeHtml(response, "true");
    }

    @RequestMapping("/pwdpage")
    public void pwdPage(HttpServletResponse response, Long id) {
        Agent agent = agentService.getAgent(id);
        WebUtils.writeJson(response, JSON.toJSONString(agent));
    }

    @RequestMapping("/editpwd")
    public void editPwd(HttpServletResponse response,Boolean type, Long id, String pwd0, String pwd1, String pwd2) {
        String result = agentService.editPwd(id,type,pwd0, pwd1, pwd2);
        WebUtils.writeHtml(response, result);
    }

    @RequestMapping("/detail")
    public String showDetail(Model model, Long id) {
        Agent agent = agentService.getAgent(id);
        if (agent == null) {
            return "/error/404";
        }
        model.addAttribute("agent", agent);
        return "/agent/detail";
    }

    @RequestMapping("/getConnAgents")
    public void getConnAgents(HttpServletResponse response) {
        List<Agent> agents = agentService.getAgentByConnType(Opencron.ConnType.CONN);
        WebUtils.writeJson(response, JSON.toJSONString(agents));
    }

    @RequestMapping("/path")
    public void getPath(HttpServletResponse response,Long agentId) {
        Agent agent = agentService.getAgent(agentId);
        String path = executeService.path(agent);
        WebUtils.writeHtml(response,path==null?"":path+"/.password");
    }
}
