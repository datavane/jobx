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

package org.opencron.server.job;

import com.alibaba.fastjson.JSON;
import org.opencron.common.utils.CommonUtils;
import org.opencron.common.utils.ParamsMap;
import org.opencron.server.domain.Agent;
import org.opencron.server.service.AgentService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.opencron.common.job.Action;
import org.opencron.common.job.Opencron;
import org.opencron.common.job.Request;
import org.opencron.common.job.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 *
 * agent OpencronCaller
 *
 * @author  <a href="mailto:benjobs@qq.com">B e n</a>
 * @version 1.0
 * @date 2016-03-27
 */

@Component
public class OpencronCaller {

    @Autowired
    private AgentService agentService;

    public Response call(Request request,Agent agent) throws Exception {

        //代理...
        if (agent.getProxy() == Opencron.ConnType.PROXY.getType()) {
            ParamsMap proxyParams = new ParamsMap();
            proxyParams.put(
                    "proxyHost",request.getHostName(),
                    "proxyPort",request.getPort(),
                    "proxyAction",request.getAction().name(),
                    "proxyPassword",request.getPassword()
            );

            if (CommonUtils.notEmpty(request.getParams())) {
                proxyParams.put("proxyParams", JSON.toJSONString(request.getParams()));
            }

            Agent proxyAgent = agentService.getAgent(agent.getProxyAgent());
            request.setHostName(proxyAgent.getIp());
            request.setPort(proxyAgent.getPort());
            request.setAction(Action.PROXY);
            request.setPassword(proxyAgent.getPassword());
            request.setParams(proxyParams);
        }

        TTransport transport;
        /**
         * ping的超时设置为5毫秒,其他默认
         */
        if (request.getAction().equals(Action.PING)) {
            transport = new TSocket(request.getHostName(),request.getPort(),1000*10);
        }else {
            transport = new TSocket(request.getHostName(),request.getPort());
        }
        TProtocol protocol = new TBinaryProtocol(transport);
        Opencron.Client client = new Opencron.Client(protocol);
        transport.open();

        Response response = null;
        for(Method method:client.getClass().getMethods()){
            if (method.getName().equalsIgnoreCase(request.getAction().name())) {
                response = (Response) method.invoke(client, request);
                break;
            }
        }

       transport.flush();
       transport.close();
       return response;
   }

}
