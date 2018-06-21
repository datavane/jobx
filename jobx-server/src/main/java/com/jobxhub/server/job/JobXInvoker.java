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

package com.jobxhub.server.job;

import com.alibaba.fastjson.JSON;
import com.jobxhub.common.Constants;
import com.jobxhub.common.ext.ExtensionLoader;
import com.jobxhub.common.job.Action;
import com.jobxhub.common.job.Request;
import com.jobxhub.common.job.Response;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.rpc.Client;
import com.jobxhub.rpc.InvokeCallback;
import com.jobxhub.rpc.Invoker;
import com.jobxhub.server.service.AgentService;
import com.jobxhub.server.dto.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import com.jobxhub.common.util.collection.HashMap;


/**
 * agent JobXCaller
 *
 * @author <a href="mailto:benjobs@qq.com">B e n</a>
 * @version 1.0
 * @date 2016-03-27
 */

@Component
public class JobXInvoker implements Invoker {

    @Autowired
    private AgentService agentService;

    //保存所有agent的client....
    private Map<String,Client> clientMap = new HashMap<String, Client>(0);

    @Override
    public Response sentSync(Request request) {
        try {
            checkProxyAgent(request);
            return getClient(request).sentSync(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void sentOneWay(Request request) {
        try {
            checkProxyAgent(request);
            getClient(request).sentOneWay(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sentAsync(Request request, InvokeCallback callback) {
        try {
            checkProxyAgent(request);
            getClient(request).sentAsync(request, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkProxyAgent(Request request) {
        if (request.getProxyId() != null) {
            HashMap<String,Object> proxyParams = new HashMap<String, Object>(0);
            proxyParams.put(Constants.PARAM_PROXYHOST_KEY, request.getHost());
            proxyParams.put(Constants.PARAM_PROXYPORT_KEY, request.getPort());
            proxyParams.put(Constants.PARAM_PROXYACTION_KEY, request.getAction().name());
            proxyParams.put(Constants.PARAM_PROXYPASSWORD_KEY, request.getPassword());

            if (CommonUtils.notEmpty(request.getParams())) {
                proxyParams.put(Constants.PARAM_PROXYPARAMS_KEY, JSON.toJSONString(request.getParams()));
            }

            Agent agent = agentService.getAgent(request.getProxyId());
            request.setHost(agent.getHost());
            request.setPort(agent.getPort());
            request.setAction(Action.PROXY);
            request.setPassword(agent.getPassword());
            request.setParams(proxyParams);
        }
    }

    private Client getClient(Request request) {
        Client client = clientMap.get(request.getAddress());
        if (client == null) {
            client =  ExtensionLoader.load(Client.class);
            clientMap.put(request.getAddress(),client);
        }
        return client;
    }



}
