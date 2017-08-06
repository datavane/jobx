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

import org.opencron.server.domain.Agent;
import org.opencron.server.domain.Group;
import org.opencron.server.job.OpencronTools;
import org.opencron.server.service.AgentService;
import org.opencron.server.service.GroupService;
import org.opencron.server.tag.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.*;


@Controller
@RequestMapping("group")
public class GroupController extends BaseController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private AgentService agentService;

    @RequestMapping("view.htm")
    public String view(PageBean pageBean) {
        groupService.getGroupPage(pageBean);
        return "/group/view";
    }

    @RequestMapping("add.htm")
    public String add(Model model) {
        List<Group> groups = groupService.getGroupforAgent();
        model.addAttribute("groups",groups);
        return "/group/add";
    }

    @RequestMapping("edit/{groupId}.htm")
    public String edit(@PathVariable("groupId")Long groupId, Model model) {
        Group group = groupService.getById(groupId);
        List<Group> groups = groupService.getGroupforAgent();
        model.addAttribute("group",group);
        model.addAttribute("groups",groups);
        return "/group/edit";
    }

    @RequestMapping(value = "checkname.do",method= RequestMethod.POST)
    @ResponseBody
    public boolean checkname(Long id, String groupName) {
        return  !groupService.existsName(id, groupName);
    }

    @RequestMapping(value = "save.do",method= RequestMethod.POST)
    public String save(HttpSession session,Group group, String agentIds){
        group.setCreateTime(new Date());
        group.setUserId(OpencronTools.getUserId(session));

        List<Agent> agents = agentService.getAgentByIds(agentIds);
        group.getAgents().addAll(agents);
        groupService.merge(group);
        return "redirect:/group/view.htm?csrf=" + OpencronTools.getCSRF(session);
    }


}
