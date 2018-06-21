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

package com.jobxhub.rpc.netty;

import com.jobxhub.common.Constants;
import com.jobxhub.common.job.Request;
import com.jobxhub.common.job.Response;
import com.jobxhub.rpc.InvokeCallback;
import com.jobxhub.rpc.RpcFuture;
import com.jobxhub.rpc.support.AbstractClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:benjobs@qq.com">B e n</a>
 * @version 1.0
 * @date 2016-03-27
 */

public class NettyClient extends AbstractClient {

    private static final NioEventLoopGroup NIO_EVENT_LOOP_GROUP = new NioEventLoopGroup(Constants.DEFAULT_IO_THREADS, new DefaultThreadFactory("NettyClientWorker", true));

    @Override
    public void connect(final Request request) {
        int timeout = 3000;
        this.bootstrap = new Bootstrap().group(NIO_EVENT_LOOP_GROUP)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout < 3000 ? 3000 : timeout)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(
                                NettyCodecAdapter.getCodecAdapter().getDecoder(Response.class),
                                NettyCodecAdapter.getCodecAdapter().getEncoder(Request.class),
                                new NettyClientHandler(NettyClient.this, request)
                        );
                    }
                });
    }

    @Override
    public Response sentSync(Request request) throws TimeoutException {
        Channel channel = getChannel(request);
        if (channel != null && channel.isActive()) {
            RpcFuture rpcFuture = new RpcFuture(request);
            channel.writeAndFlush(request).addListener(new FutureListener(rpcFuture));
            return rpcFuture.get();
        } else {
            throw new IllegalArgumentException("[JobX] NettyRPC invokeSync channel not active. request id:" + request.getId());
        }
    }

    @Override
    public void sentAsync(Request request, final InvokeCallback callback) throws Exception {
        Channel channel = getChannel(request);
        if (channel != null && channel.isActive()) {
            RpcFuture rpcFuture = new RpcFuture(request, callback);
            channel.writeAndFlush(request).addListener(new FutureListener(rpcFuture));
        } else {
            throw new IllegalArgumentException("[JobX] NettyRPC invokeAsync channel not active. request id:" + request.getId());
        }
    }

    @Override
    public void sentOneWay(Request request) throws Exception {
        Channel channel = getChannel(request);
        if (channel != null && channel.isActive()) {
            RpcFuture rpcFuture = new RpcFuture(request);
            channel.writeAndFlush(request).addListener(new FutureListener(rpcFuture));
        } else {
            throw new IllegalArgumentException("[JobX] NettyRPC invokeAsync invokeOneway channel not active. request id:" + request.getId());
        }
    }

}
