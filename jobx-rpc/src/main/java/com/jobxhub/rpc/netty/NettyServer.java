/**
 * Copyright (c) 2015 The JobX Project
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with work for additional information
 * regarding copyright ownership. The ASF licenses file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use file except in compliance
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

package com.jobxhub.rpc.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.DefaultThreadFactory;
import com.jobxhub.common.Constants;
import com.jobxhub.common.job.Request;
import com.jobxhub.common.job.Response;
import org.slf4j.LoggerFactory;
import com.jobxhub.rpc.ServerHandler;
import com.jobxhub.rpc.Server;
import org.slf4j.Logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jobxhub.common.util.ExceptionUtils.stackTrace;

/**
 * @author benjobs
 */
public class NettyServer implements Server {

    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    protected static ThreadPoolExecutor threadPoolExecutor;

    protected final HashedWheelTimer timer = new HashedWheelTimer();

    private Channel channel;

    private Thread serverDaemon;

    public NettyServer() {
    }

    @Override
    public void  start(final int port, final ServerHandler serverHandler) {
        serverDaemon = new Thread(new Runnable() {
            @Override
            public void run() {
                bootstrap = new ServerBootstrap();
                bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyServerBoss", true));
                workerGroup = new NioEventLoopGroup(Constants.DEFAULT_IO_THREADS, new DefaultThreadFactory("NettyServerWorker", true));
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                        .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                        .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel channel) throws Exception {
                                channel.pipeline().addLast(
                                        new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 0),
                                        NettyCodecAdapter.getCodecAdapter().getDecoder(Request.class),
                                        NettyCodecAdapter.getCodecAdapter().getEncoder(Response.class),
                                        new NettyServerHandler(serverHandler)
                                );
                            }
                        });
                // bind
                ChannelFuture channelFuture = bootstrap.bind(port);
                channelFuture.syncUninterruptibly();
                channel = channelFuture.channel();
            }
        });
        serverDaemon.setDaemon(true);
        serverDaemon.start();

        threadPoolExecutor = new ThreadPoolExecutor(50, 100, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            private final AtomicInteger idGenerator = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "NettyServer" + idGenerator.incrementAndGet());
            }
        });
    }

    @Override
    public void destroy() throws Throwable {
        try {
            if (channel != null) {
                // unbind.
                channel.close();
            }
            if (bootstrap != null) {
                threadPoolExecutor.shutdown();
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                if (logger.isInfoEnabled()) {
                    logger.error("[JobX] NettyServer stop done");
                }
            }
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("[JobX] NettyServer stop error:{}", stackTrace(e));
            }
        }finally {
            serverDaemon.interrupt();
        }
    }

}
