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

import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.collection.HashMap;
import com.jobxhub.server.annotation.RequestRepeat;
import com.jobxhub.server.service.*;
import com.jobxhub.server.support.JobXTools;
import com.jobxhub.server.tag.PageBean;
import com.jobxhub.server.dto.Role;
import com.jobxhub.server.dto.Status;
import com.jobxhub.server.dto.User;
import com.jobxhub.server.dto.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by ChenHui on 2016/2/18.
 */
@Controller
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserAgentService userAgentService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private ConfigService configService;

    @RequestMapping("view.htm")
    public String view(PageBean pageBean) {
        userService.getPageBean(pageBean);
        return "/user/view";
    }

    @RequestMapping("detail/{userId}.htm")
    public String detail(@PathVariable("userId") Long userId, Model model) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return "/error/404";
        }
        model.addAttribute("u", user);
        return "/user/detail";
    }

    @RequestMapping("add.htm")
    public String add(Model model) {
        List<Role> role = roleService.getAll();
        model.addAttribute("role", role);
        model.addAttribute("agents", agentService.getAll());
        model.addAttribute("execUser",configService.getExecUser());
        return "/user/add";
    }

    @RequestMapping(value = "add.do", method = RequestMethod.POST)
    @RequestRepeat(view = true)
    public String add(User user) {
        userService.addUser(user);
        return "redirect:/user/view.htm";
    }

    @RequestMapping("edit/{id}.htm")
    public String editPage(HttpSession session, Model model, @PathVariable("id") Long id) {
        if (!JobXTools.isPermission(session)
                && !JobXTools.getUserId(session).equals(id)) {
            return String.format("redirect:/user/detail/%d.htm", id);
        }

        User user = userService.getUserById(id);
        if (user == null) {
            return "/error/404";
        }

        List<UserAgent> userAgent = userAgentService.getUserAgent(id);
        model.addAttribute("u", user);
        model.addAttribute("role", roleService.getAll());
        model.addAttribute("agents", agentService.getAll());
        model.addAttribute("userAgent", userAgent);

        Map<String,Boolean> execUser = new HashMap<String,Boolean>(0);
        List<String> allExecUser = configService.getExecUser();
        if (CommonUtils.notEmpty(allExecUser)) {
            for (String _execUser:allExecUser) {
                execUser.put(_execUser,user.getExecUser()!=null&&user.getExecUser().contains(_execUser));
            }
        }
        model.addAttribute("execUser",execUser);
        return "/user/edit";
    }

    @RequestMapping(value = "edit.do", method = RequestMethod.POST)
    @RequestRepeat(view = true)
    public String edit(HttpSession session, User user) {
        User user1 = userService.getUserById(user.getUserId());
        user1.setRoleId(user.getRoleId());
        user1.setRealName(user.getRealName());
        user1.setContact(user.getContact());
        user1.setEmail(user.getEmail());
        user1.setQq(user.getQq());
        user1.setModifyTime(new Date());
        user1.setExecUser(user.getExecUser());
        userService.updateUser(session,user1);
        return "redirect:/user/view.htm";
    }

    @RequestMapping(value = "get.do", method = RequestMethod.POST)
    @ResponseBody
    public User get(Long id) {
        return userService.getUserById(id);
    }

    @RequestMapping(value = "pwd.do", method = RequestMethod.POST)
    @ResponseBody
    @RequestRepeat
    public String pwd(Long id, String pwd0, String pwd1, String pwd2) {
        return userService.editPassword(id, pwd0, pwd1, pwd2);
    }

    @RequestMapping(value = "checkname.do", method = RequestMethod.POST)
    @ResponseBody
    public Status checkName(String name) {
        return Status.create(!userService.existsName(name));
    }
}
