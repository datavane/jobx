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

import org.apache.dubbo.config.annotation.Reference;
import com.jobxhub.common.Constants;
import com.jobxhub.service.api.ConfigService;
import com.jobxhub.service.api.RecordService;
import com.jobxhub.service.model.Config;
import com.jobxhub.service.vo.RestResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by ChenHui on 2016/2/17.
 */
@RestController
@RequestMapping("profile")
public class ProfileController {

    @Reference
    private ConfigService configService;

    @Reference
    private RecordService recordService;

    @PostMapping("info")
    public RestResult info() {
        Config config = configService.getSysConfig();
        return RestResult.ok(config);
    }

    @PostMapping("save")
    public RestResult edit(Config config) {
        configService.update(config);
        return RestResult.ok();
    }

    @PostMapping("clean")
    public RestResult clearRecord(String start, String end) {
        recordService.deleteRecord(start, end);
        return RestResult.ok();
    }

    @PostMapping("skin")
    public RestResult skin(String skin, HttpSession session) {
        session.setAttribute(Constants.PARAM_SKIN_NAME_KEY, skin);
        return RestResult.ok();
    }

}