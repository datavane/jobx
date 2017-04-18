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

import com.alibaba.fastjson.JSON;
import org.opencron.server.job.OpencronTools;
import org.opencron.server.service.AgentService;
import org.opencron.server.tag.PageBean;
import org.opencron.common.utils.WebUtils;
import org.opencron.server.domain.Role;
import org.opencron.server.domain.User;
import org.opencron.server.service.UserService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

import static org.opencron.common.utils.CommonUtils.notEmpty;

/**
 * Created by ChenHui on 2016/2/18.
 */
@Controller
@RequestMapping("/user")
public class UserController  extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private AgentService agentService;

    @RequestMapping("/view")
    public String queryUser(PageBean pageBean) {
        userService.queryUser(pageBean);
        return "/user/view";
    }

    @RequestMapping("/detail")
    public String detail(Long userId,Model model) {
        User user = userService.queryUserById(userId);
        model.addAttribute("u", user);
        return "/user/detail";
    }

    @RequestMapping("addpage")
    public String addPage(Model model) {
        List<Role> role = userService.getRoleGroup();
        model.addAttribute("role", role);
        model.addAttribute("agents", agentService.getAll());
        return "/user/add";
    }

    @RequestMapping("add")
    public String add(HttpSession session, User user) {
        userService.addUser(user);
        return "redirect:/user/view?csrf=" + OpencronTools.getCSRF(session);
    }

    @RequestMapping("/editpage")
    public String editPage(HttpSession session,Model model, Long id) {
        if (!OpencronTools.isPermission(session)
                && !OpencronTools.getUserId(session).equals(id)){
            return "redirect:/user/detail?csrf="+ OpencronTools.getCSRF(session);
        }
        model.addAttribute("u", userService.queryUserById(id));
        model.addAttribute("role", userService.getRoleGroup());
        model.addAttribute("agents", agentService.getAll());
        return "/user/edit";
    }

    @RequestMapping("/edit")
    public String edit(HttpSession session, User user) throws SchedulerException {
        User user1 = userService.getUserById(user.getUserId());
        if (notEmpty(user.getRoleId()) && OpencronTools.isPermission(session)) {
            user1.setRoleId(user.getRoleId());
        }
        user1.setAgentIds(user.getAgentIds());
        user1.setRealName(user.getRealName());
        user1.setContact(user.getContact());
        user1.setEmail(user.getEmail());
        user1.setQq(user.getQq());
        user1.setModifyTime(new Date());
        userService.updateUser(user1);
        return "redirect:/user/view?csrf="+ OpencronTools.getCSRF(session);
    }

    @RequestMapping("/pwdpage")
    public void pwdPage(HttpServletResponse response, Long id) {
        User user = userService.queryUserById(id);
        WebUtils.writeJson(response, JSON.toJSONString(user));
    }

    @RequestMapping("/editpwd")
    public void editPwd(HttpServletResponse response, Long id, String pwd0, String pwd1, String pwd2) {
        String result = userService.editPwd(id, pwd0, pwd1, pwd2);
        WebUtils.writeHtml(response, result);
    }

    @RequestMapping("/checkname")
    public void checkName(HttpServletResponse response, String name) {
        String result = userService.checkName(name);
        WebUtils.writeHtml(response, result);
    }
}
