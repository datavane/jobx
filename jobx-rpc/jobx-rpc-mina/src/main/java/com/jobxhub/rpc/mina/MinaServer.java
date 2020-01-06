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


import com.jobxhub.common.job.Request;
import com.jobxhub.common.job.Response;
import com.jobxhub.rpc.Server;
import com.jobxhub.rpc.ServerHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jobxhub.common.util.ExceptionUtils.stackTrace;

public class MinaServer implements Server {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private NioSocketAcceptor acceptor;

    private InetSocketAddress socketAddress;

    protected static ThreadPoolExecutor threadPoolExecutor;

    private Thread serverDaemon = null;

    @Override
    public void start(final int port,final ServerHandler handler) {

        this.serverDaemon = new Thread(new Runnable() {
            @Override
            public void run() {
                final MinaServerHandler serverHandler = new MinaServerHandler(handler);
                socketAddress = new InetSocketAddress(port);

                acceptor = new NioSocketAcceptor();
                acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
                acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MinaCodecAdapter(Response.class, Request.class)));
                acceptor.setHandler(serverHandler);
                try {
                    acceptor.bind(socketAddress);
                    if (logger.isInfoEnabled()) {
                        logger.info("[JobX] MinaServer start at address:{} success", port);
                    }
                } catch (IOException e) {
                    logger.error("[JobX] MinaServer start failure: {}", stackTrace(e));
                }
            }
        });
        this.serverDaemon.setDaemon(true);
        this.serverDaemon.start();

        threadPoolExecutor = new ThreadPoolExecutor(50, 100, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            private final AtomicInteger idGenerator = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "MinaServer" + this.idGenerator.incrementAndGet());
            }
        });

    }

    @Override
    public void destroy() throws Throwable {
        try {
            if (acceptor != null) {
                acceptor.dispose();
            }
            this.serverDaemon.interrupt();
            if (logger.isInfoEnabled()) {
                logger.info("[JobX] MinaServer stoped!");
            }
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("[JobX] MinaServer stop error:{}", stackTrace(e));
            }
        }
    }

}
