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

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import com.jobxhub.common.job.Response;
import com.jobxhub.common.logging.LoggerFactory;
import com.jobxhub.rpc.RpcFuture;
import org.slf4j.Logger;

/**
 * @author benjobs
 */
public class MinaClientHandler extends IoHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MinaClientHandler.class);

    private MinaClient minaClient;

    public MinaClientHandler(MinaClient minaClient) {
        this.minaClient = minaClient;
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        Response response = (Response) message;
        if (logger.isInfoEnabled()) {
            logger.info("[JobX] minaRPC client receive response id:{}", response.getId());
        }
        RpcFuture rpcFuture = this.minaClient.getRpcFuture(response.getId());
        rpcFuture.received(response);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
    }

}

