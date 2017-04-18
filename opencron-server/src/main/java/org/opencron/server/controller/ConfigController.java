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

import org.opencron.server.domain.Config;
import org.opencron.server.job.OpencronTools;
import org.opencron.server.service.ConfigService;
import org.opencron.server.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * Created by ChenHui on 2016/2/17.
 */
@Controller
@RequestMapping("config")
public class ConfigController  extends BaseController{

    @Autowired
    private ConfigService configService;

    @Autowired
    private RecordService recordService;

    @RequestMapping("/view")
    public String settings(Model model) {
        model.addAttribute("config", configService.getSysConfig());
        return "config/view";
    }

    @RequestMapping("/editpage")
    public String editPage(Model model) {
        model.addAttribute("config", configService.getSysConfig());
        return "config/edit";
    }

    @RequestMapping("/edit")
    public String edit(HttpSession session, Config config) {
        config.setConfigId(configService.getSysConfig().getConfigId());
        config.setTemplate(config.getTemplate());
        config.setSendUrl(config.getSendUrl());
        configService.update(config);
        return "redirect:/config/view?csrf="+ OpencronTools.getCSRF(session);
    }

    @RequestMapping("/clear")
    public void clearRecord(String startTime,String endTime) {
        recordService.deleteRecordBetweenTime(startTime, endTime);
    }

}
