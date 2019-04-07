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
import com.jobxhub.service.api.AgentService;
import com.jobxhub.service.api.UserService;
import com.jobxhub.service.model.Agent;
import com.jobxhub.service.model.Job;
import com.jobxhub.service.model.User;
import com.jobxhub.service.vo.PageBean;
import com.jobxhub.service.vo.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/agent")
@Slf4j
public class AgentController {

    @Reference
    private AgentService agentService;

    @PostMapping("/all")
    public RestResult all() {
        List<Agent> agents = agentService.getAll();
        return RestResult.ok(agents);
    }
}
