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

import org.opencron.common.utils.DigestUtils;
import org.opencron.server.domain.Config;
import org.opencron.server.job.OpencronTools;
import org.opencron.server.service.ConfigService;
import org.opencron.server.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @RequestMapping(value = "edit.do",method= RequestMethod.POST)
    public String edit(HttpSession session, Config config) {
        Config cfg = configService.getSysConfig();
        cfg.setSenderEmail(config.getSenderEmail());
        cfg.setConfigId(configService.getSysConfig().getConfigId());
        cfg.setTemplate(DigestUtils.passBase64(config.getTemplate()));
        cfg.setSendUrl(DigestUtils.passBase64(config.getSendUrl()));
        cfg.setPassword(config.getPassword());
        cfg.setSmtpHost(config.getSmtpHost());
        cfg.setSpaceTime(config.getSpaceTime());
        cfg.setSmtpPort(config.getSmtpPort());
        configService.update(cfg);
        return "redirect:/config/view.htm?csrf=" + OpencronTools.getCSRF(session);
    }

    @RequestMapping(value = "clear.do",method= RequestMethod.POST)
    @ResponseBody
    public boolean clearRecord(String startTime, String endTime) {
        recordService.deleteRecordBetweenTime(startTime, endTime);
        return true;
    }

    @RequestMapping(value = "skin.do",method= RequestMethod.POST)
    @ResponseBody
    public boolean skin(String skin, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        session.setAttribute(OpencronTools.SKIN_NAME,skin);
        return true;
    }

}
