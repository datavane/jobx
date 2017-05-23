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

package org.opencron.common.rpc.core;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.opencron.common.rpc.model.PingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * refer: https://netty.io/4.1/api/io/netty/handler/timeout/IdleStateHandler.html
 *
 * @author Ricky Fung
 */
public class ConnectionHandler extends ChannelDuplexHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;

            if (e.state() == IdleState.READER_IDLE) {
                logger.info("READER_IDLE 事件触发, 关闭连接");/*读超时*/
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                logger.info("WRITER_IDLE 事件触发");
                ctx.writeAndFlush(new PingMessage());
            } else if (e.state() == IdleState.ALL_IDLE) {
                logger.info("ALL_IDLE 事件触发, 关闭连接");
                ctx.close();
            }
        }
    }
}
