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
import com.jobxhub.server.dto.RestResult;
import com.jobxhub.server.service.ConfigService;
import com.jobxhub.server.service.RecordService;
import com.jobxhub.server.dto.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * Created by ChenHui on 2016/2/17.
 */
@RestController
@RequestMapping("profile")
public class ProfileController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private RecordService recordService;

    @RequestMapping(value = "info.do", method = RequestMethod.POST)
    public RestResult info() {
        Config config = configService.getSysConfig();
        return RestResult.rest(200,config);
    }

    @RequestMapping(value = "save.do", method = RequestMethod.POST)
    public RestResult edit(Config config) {
        configService.update(config);
        return RestResult.rest(200);
    }

    @RequestMapping(value = "clear.do", method = RequestMethod.POST)
    public RestResult clearRecord(String start, String end) {
        recordService.deleteRecord(start, end);
        return RestResult.rest(200);
    }

    @RequestMapping(value = "skin.do", method = RequestMethod.POST)
    @ResponseBody
    public Status skin(String skin, HttpSession session) {
        session.setAttribute(Constants.PARAM_SKIN_NAME_KEY, skin);
        return Status.TRUE;
    }

}
