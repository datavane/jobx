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
import com.jobxhub.common.util.collection.ParamsMap;
import com.jobxhub.server.dto.Agent;
import com.jobxhub.server.util.PageUtils;
import com.jobxhub.server.service.AgentService;
import com.jobxhub.server.service.ExecuteService;
import com.jobxhub.server.dto.Status;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("verify")
public class VerifyController extends BaseController {

    @Autowired
    private ExecuteService executeService;

    @Autowired
    private AgentService agentService;

    @RequestMapping(value = "exp.do", method = RequestMethod.POST)
    @ResponseBody
    public Status validateCronExp(String cronExp) {
        boolean pass = CronExpression.isValidExpression(cronExp);
        return Status.create(pass);
    }

    @RequestMapping(value = "recenttime.do", method = RequestMethod.POST)
    @ResponseBody
    public List<String> getRecentTriggerTime(String cronExp) {
       return PageUtils.getRecentTriggerTime(cronExp);
    }

    /**
     * @return
     */
    @RequestMapping(value = "ping.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Integer> validatePing(Agent agent) {
        Map<String,Integer> result = new HashMap<String, Integer>(0);
        if (hasProxy(agent, result)) return result;
        Constants.ConnStatus connStatus = executeService.ping(agent,false);
        result.put("status",connStatus.getValue());
        return result;
    }

    @RequestMapping(value = "macid.do", method = RequestMethod.POST)
    @ResponseBody
    public Map macid(Agent agent) {
        Map<String,Integer> result = new HashMap<String, Integer>(0);
        if (hasProxy(agent, result)) return result;
        String macId = executeService.getMacId(agent);
        if (macId == null) {
            return ParamsMap.map().set("status", false);
        }
        return ParamsMap.map().set("status", true).set("macId", macId);
    }

    private boolean hasProxy(Agent agent, Map<String, Integer> result) {
        if (agent.getProxy()) {
            Agent proxyAgent = agentService.getAgent(agent.getProxyId());
            if (proxyAgent == null) {
                result.put("status",Constants.ConnStatus.DISCONNECTED.getValue());
                return true;
            }
        }else {
            agent.setProxyId(null);
        }
        return false;
    }
}
