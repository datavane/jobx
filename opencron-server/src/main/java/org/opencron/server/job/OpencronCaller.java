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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.opencron.common.rpc.codec.RpcDecoder;
import org.opencron.common.rpc.codec.RpcEncoder;
import org.opencron.common.rpc.core.ChannelWrapper;
import org.opencron.common.rpc.core.InvokeCallback;
import org.opencron.common.rpc.core.ConnectionHandler;
import org.opencron.common.rpc.core.RpcFuture;
import org.opencron.common.rpc.model.Request;
import org.opencron.common.rpc.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * agent OpencronCaller
 *
 * @author <a href="mailto:benjobs@qq.com">B e n</a>
 * @version 1.0
 * @date 2016-03-27
 */

@Component
public class OpencronCaller {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    protected ConcurrentHashMap<String, RpcFuture> rpcFutureTable;

    private ConcurrentHashMap<String, ChannelWrapper> channelTable;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    public void start() {

        group = new NioEventLoopGroup();

        bootstrap = new Bootstrap();

        this.rpcFutureTable = new ConcurrentHashMap<String, RpcFuture>(256);

        this.channelTable = new ConcurrentHashMap<String, ChannelWrapper>();

        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                                new LengthFieldPrepender(4),
                                new RpcDecoder(Response.class), //
                                new RpcEncoder(Request.class), //
                                new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS),
                                new ConnectionHandler(),
                                new OpencronHandler());
                    }
                });

        this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
            private final AtomicInteger idGenerator = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {

                return new Thread(r, "Rpc-Scheduled-" + this.idGenerator.incrementAndGet());
            }
        });

        this.scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                scanRpcFutureTable();
            }
        }, 500, 500, TimeUnit.MILLISECONDS);
    }

    public void shutdown(){
        this.scheduledThreadPoolExecutor.shutdown();
        this.group.shutdownGracefully();
    }

    public Response sendSync(final Request request, long timeout, TimeUnit unit) throws Exception {

        this.start();

        Channel channel = getOrCreateChannel(request);

        if (channel != null && channel.isActive()) {

            final RpcFuture<Response> rpcFuture = new RpcFuture<Response>(timeout, unit);

            this.rpcFutureTable.put(request.getId(), rpcFuture);
            //写数据
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        logger.info("send success, request id:{}", request.getId());
                        rpcFuture.setSendRequestSuccess(true);
                        return;
                    } else {
                        logger.info("send failure, request id:{}", request.getId());
                        rpcFutureTable.remove(request.getId());
                        rpcFuture.setSendRequestSuccess(false);
                        rpcFuture.setFailure(future.cause());
                    }
                }
            });

            this.shutdown();

            return rpcFuture.get(timeout, unit);
        } else {
            throw new IllegalArgumentException("channel not active. request id:"+request.getId());
        }

    }

    /**
     * 异步
     * @param request
     * @param timeout
     * @param unit
     * @param callback
     * @throws Exception
     */
    public void sendAsync(final Request request, long timeout, TimeUnit unit, final InvokeCallback callback) throws Exception {

        this.start();

        Channel channel = getOrCreateChannel(request);
        if (channel != null && channel.isActive()) {

            final RpcFuture<Response> rpcFuture = new RpcFuture<Response>(timeout, unit, callback);
            this.rpcFutureTable.put(request.getId(), rpcFuture);
            //写数据
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        logger.info("send success, request id:{}", request.getId());
                        rpcFuture.setSendRequestSuccess(true);
                        return;
                    } else {
                        logger.info("send failure, request id:{}", request.getId());

                        rpcFutureTable.remove(request.getId());
                        rpcFuture.setSendRequestSuccess(false);
                        rpcFuture.setFailure(future.cause());
                        //回调
                        callback.onFailure(future.cause());
                    }
                }
            });
        } else {
            throw new IllegalArgumentException("channel not active. request id:"+request.getId());
        }

        this.shutdown();
    }

    /**
     * 单向
     * @param request
     * @param timeout
     * @param unit
     */
    public void sendOneway(final Request request, long timeout, TimeUnit unit){

        this.start();

        Channel channel = getOrCreateChannel(request);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        logger.info("send success, request id:{}", request.getId());
                    } else {
                        logger.info("send failure, request id:{}", request.getId(), future);
                    }
                }
            });
        } else {
            throw new IllegalArgumentException("channel not active. request id:"+request.getId());
        }

        this.shutdown();
    }

    class OpencronHandler extends SimpleChannelInboundHandler<Response> {

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response msg) throws Exception {

            final Response response = msg;
            logger.info("Rpc client receive response id:{}", response.getId());
            RpcFuture future = rpcFutureTable.get(response.getId());

            future.setResult(response);
            if(future.isAsync()){   //异步调用
                logger.info("Rpc client async callback invoke");
                future.execCallback();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            logger.error("捕获异常", cause);
        }
    }

    private Channel getOrCreateChannel(Request request){

        String addr = request.getHostName()+":"+request.getPort();
        ChannelWrapper cw = this.channelTable.get(addr);
        if (cw != null && cw.isActive()) {
            return cw.getChannel();
        }

        synchronized (this){
            // 发起异步连接操作
            InetSocketAddress address = new InetSocketAddress(request.getHostName(),request.getPort());
            ChannelFuture channelFuture = bootstrap.connect(address);
            cw = new ChannelWrapper(channelFuture);
            this.channelTable.put(addr, cw);
        }
        if (cw != null) {
            ChannelFuture channelFuture = cw.getChannelFuture();
            long timeout = 5000;
            if (channelFuture.awaitUninterruptibly(timeout)) {
                if (cw.isActive()) {
                    logger.info("createChannel: connect remote host[{}] success, {}", addr, channelFuture.toString());
                    return cw.getChannel();
                } else {
                    logger.warn("createChannel: connect remote host[" + addr + "] failed, " + channelFuture.toString(), channelFuture.cause());
                }
            } else {
                logger.warn("createChannel: connect remote host[{}] timeout {}ms, {}", addr, timeout, channelFuture);
            }
        }
        return null;
    }

    /**定时清理超时Future**/
    private void scanRpcFutureTable() {
        final List<RpcFuture> timeoutReqList = new ArrayList<RpcFuture>();
        Iterator<Map.Entry<String, RpcFuture>> it = this.rpcFutureTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, RpcFuture> next = it.next();
            RpcFuture rep = next.getValue();

            if ((rep.getBeginTimestamp() + rep.getTimeoutMillis() + 1000) <= System.currentTimeMillis()) {  //超时
                it.remove();
                timeoutReqList.add(rep);
            }
        }

        for (RpcFuture future : timeoutReqList) {
            //释放资源
        }
    }


}
