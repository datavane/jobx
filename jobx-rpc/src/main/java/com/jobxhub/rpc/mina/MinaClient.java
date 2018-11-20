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
package com.jobxhub.rpc.mina;

import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import com.jobxhub.common.job.Request;
import com.jobxhub.common.job.Response;
import com.jobxhub.rpc.InvokeCallback;
import com.jobxhub.rpc.RpcFuture;
import com.jobxhub.rpc.support.AbstractClient;


@Slf4j
public class MinaClient extends AbstractClient {

    @Override
    public void connect(Request request) {
        if (connector == null) {
            connector = new NioSocketConnector();
            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MinaCodecAdapter(Request.class, Response.class)));
            connector.setHandler(new MinaClientHandler(this));
            connector.setConnectTimeoutMillis(5000);
            DefaultSocketSessionConfig sessionConfiguration = (DefaultSocketSessionConfig) connector.getSessionConfig();
            sessionConfiguration.setTcpNoDelay(true);
            sessionConfiguration.setKeepAlive(true);
            sessionConfiguration.setWriteTimeout(5);
        }
    }

    @Override
    public Response sentSync(final Request request) throws Exception {
        final ConnectFuture connect = super.getConnect(request);
        if (connect != null && connect.isConnected()) {
            RpcFuture rpcFuture = new RpcFuture(request);
            //写数据
            connect.addListener(new AbstractClient.FutureListener(rpcFuture));
            IoSession session = connect.getSession();
            session.write(request);
            return rpcFuture.get();
        } else {
            throw new IllegalArgumentException("[JobX] MinaRPC channel not active. request id:" + request.getId());
        }
    }

    @Override
    public void sentAsync(final Request request, final InvokeCallback callback) throws Exception {
        final ConnectFuture connect = super.getConnect(request);
        if (connect != null && connect.isConnected()) {
            RpcFuture rpcFuture = new RpcFuture(request,callback);
            connect.addListener(new AbstractClient.FutureListener(rpcFuture));
            connect.getSession().write(request);
        } else {
            throw new IllegalArgumentException("[JobX] MinaRPC invokeAsync channel not active. request id:" + request.getId());
        }
    }

    @Override
    public void sentOneWay(final Request request) throws Exception {
        ConnectFuture connect = super.getConnect(request);
        if (connect != null && connect.isConnected()) {
            RpcFuture rpcFuture = new RpcFuture(request);
            connect.addListener(new AbstractClient.FutureListener(rpcFuture));
            connect.getSession().write(request);
        } else {
            throw new IllegalArgumentException("[JobX] MinaRPC channel not active. request id:" + request.getId());
        }
    }


}
