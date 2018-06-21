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

import com.jobxhub.common.Constants;
import com.jobxhub.server.annotation.RequestRepeat;
import com.jobxhub.server.dto.Config;
import com.jobxhub.server.service.ConfigService;
import com.jobxhub.server.service.RecordService;
import com.jobxhub.server.dto.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by ChenHui on 2016/2/17.
 */
@Controller
@RequestMapping("config")
public class ConfigController extends BaseController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private RecordService recordService;

    @RequestMapping("view.htm")
    public String settings(Model model) {
        model.addAttribute("config", configService.getSysConfig());
        return "config/view";
    }

    @RequestMapping("edit.htm")
    public String editPage(Model model) {
        model.addAttribute("config", configService.getSysConfig());
        return "config/edit";
    }

    @RequestMapping(value = "edit.do", method = RequestMethod.POST)
    @RequestRepeat(view = true)
    public String edit(Config config) {
        configService.update(config);
        return "redirect:/config/view.htm";
    }

    @RequestMapping(value = "clear.do", method = RequestMethod.POST)
    @ResponseBody
    public Status clearRecord(String startTime, String endTime) {
        recordService.deleteRecord(startTime, endTime);
        return Status.TRUE;
    }

    @RequestMapping(value = "skin.do", method = RequestMethod.POST)
    @ResponseBody
    public Status skin(String skin, HttpSession session) {
        session.setAttribute(Constants.PARAM_SKIN_NAME_KEY, skin);
        return Status.TRUE;
    }

}
