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
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.opencron.common.serialization.OpencronDecoder;
import org.opencron.common.serialization.ResponseEncoder;
import org.opencron.common.utils.CommonUtils;
import org.opencron.common.utils.ParamsMap;
import org.opencron.server.domain.Agent;
import org.opencron.server.service.AgentService;
import org.opencron.common.job.Action;
import org.opencron.common.job.Opencron;
import org.opencron.common.job.Request;
import org.opencron.common.job.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * agent OpencronCaller
 *
 * @author <a href="mailto:benjobs@qq.com">B e n</a>
 * @version 1.0
 * @date 2016-03-27
 */

@Component
public class OpencronCaller {

    @Autowired
    private AgentService agentService;

    private void start( String host,int port, final OpencronHandler handler) throws Exception{
        //配置客户端线程组
        EventLoopGroup group=new NioEventLoopGroup();
        try{
            Bootstrap bootstrap=new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new OpencronDecoder<Response>(Response.class, 1024 * 1024, 4, 4));
                            ch.pipeline().addLast(new ResponseEncoder());
                            ch.pipeline().addLast(handler);
                        }
                    });

            ChannelFuture channelFuture=bootstrap.connect(host,port).sync();
            channelFuture.channel().closeFuture().sync();
            channelFuture.channel().close();
        }finally{
            group.shutdownGracefully();
        }
    }

    /**
     * 异步请求...
     * @param request
     * @param agent
     * @return
     * @throws Exception
     */

    public Response asyncCall(final Request request, Agent agent) throws Exception {
        //代理...
        if (agent.getProxy() == Opencron.ConnType.PROXY.getType()) {
            ParamsMap proxyParams = new ParamsMap();
            proxyParams.put(
                    "proxyHost", request.getHostName(),
                    "proxyPort", request.getPort(),
                    "proxyAction", request.getAction().name(),
                    "proxyPassword", request.getPassword()
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

        final List<Response> responseList = new ArrayList<Response>(1);
        this.start(request.getHostName(),request.getPort(),new OpencronHandler(){
            @Override
            public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
                channelHandlerContext.writeAndFlush(request);
            }

            @Override
            public void channelRead0(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {
                responseList.add(response);
                channelHandlerContext.close();
            }
        });
        return responseList.get(0);
    }

}
