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

import com.jobxhub.rpc.support.ChannelWrapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author benjobs
 */
@Slf4j
public class NettyChannelWrapper implements ChannelWrapper {

    private final ChannelFuture channelFuture;

    public NettyChannelWrapper(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

    public boolean isActive() {
        return (this.channelFuture.channel() != null && this.channelFuture.channel().isActive());
    }

    public boolean isWritable() {
        return this.channelFuture.channel().isWritable();
    }


    public Channel getChannel() {
        return this.channelFuture.channel();
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public void close() {
        getChannel().close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.info("[JobX]closeChannel: close the connection to remote address:{}, result: {}",
                            getChannel().remoteAddress(), future.isSuccess());
            }
        });
    }
}