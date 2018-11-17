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

import com.jobxhub.common.exception.RpcException;
import io.netty.channel.*;
import com.jobxhub.common.Constants;
import com.jobxhub.common.job.*;
import lombok.extern.slf4j.Slf4j;
import com.jobxhub.common.util.IOUtils;
import com.jobxhub.rpc.ServerHandler;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author benjobs
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<Request> {

    private ServerHandler handler;

    private volatile long start = 0;

    private RandomAccessFile randomAccessFile;

    public NettyServerHandler(ServerHandler handler) {
        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext handlerContext) {
        log.info("[JobX] agent channelActive Active...");
        handlerContext.fireChannelActive();
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext handlerContext, final Request request) throws Exception {

        NettyServer.threadPoolExecutor.submit(new Runnable() {

            @Override
            public void run() {

                log.debug("[JobX]Receive request {}" + request.getId());

                if (!request.getAction().equals(Action.UPLOAD)) {
                    Response response = handler.handle(request);
                    if (request.getRpcType() != RpcType.ONE_WAY) {
                        handlerContext.writeAndFlush(response).addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                log.info("[JobX] Send response for request id:{},action:{}", request.getId(), request.getAction());
                            }
                        });
                    }
                    return;
                }

                Response response = Response.response(request).setExitCode(Constants.ExitCode.SUCCESS_EXIT.getValue()).setSuccess(true);

                final RequestFile requestFile = request.getUploadFile();

                if (requestFile.getBytes() == null||requestFile.getEndPos() == -1) return;

                //first
                if (start == 0) {
                    File savePath = new File(requestFile.getSavePath());
                    if (!savePath.exists()) {
                        ResponseFile responseFile = new ResponseFile(start, requestFile.getFileMD5());
                        responseFile.setEnd(true);
                        response.setExitCode(Constants.ExitCode.NOTFOUND.getValue()).setSuccess(false).setUploadFile(responseFile).end();
                        handlerContext.writeAndFlush(response).addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                log.info("[JobX] Netty upload file {} error!savePath is not found MD5:{}", requestFile.getFile().getName(), requestFile.getFileMD5());
                            }
                        });
                        return;
                    }
                    File file = new File(savePath, requestFile.getFile().getName());

                    try {
                        if (file.exists()) {
                            String existMD5 = IOUtils.getFileMD5(file);
                            if (existMD5.equals(requestFile.getFileMD5())) {
                                ResponseFile responseFile = new ResponseFile(start, requestFile.getFileMD5());
                                responseFile.setEnd(true);
                                handlerContext.writeAndFlush(response.setUploadFile(responseFile).end()).addListener(new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                        log.info("[JobX] Netty upload file {} exists! MD5:{}", requestFile.getFile().getName(), requestFile.getFileMD5());
                                    }
                                });
                                return;
                            }
                        }
                        randomAccessFile = new RandomAccessFile(file, "rw");
                        randomAccessFile.seek(start);
                        randomAccessFile.write(requestFile.getBytes());
                        start = start + requestFile.getEndPos();

                        if (requestFile.getEndPos() > 0 && start < requestFile.getFileSize()) {
                            final ResponseFile responseFile = new ResponseFile(start, requestFile.getFileMD5(), (start * 100) / requestFile.getFileSize());
                            responseFile.setReadBuffer(requestFile.getReadBuffer());
                            handlerContext.writeAndFlush(response.setUploadFile(responseFile).end()).addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                    log.info("[JobX] Netty upload progress:{}!for request id:{},action:{}", responseFile.getProgress(), request.getId(), request.getAction());
                                }
                            });
                        } else {
                            ResponseFile responseFile = new ResponseFile(start, requestFile.getFileMD5());
                            responseFile.setEnd(true);
                            handlerContext.writeAndFlush(response.setUploadFile(responseFile).end()).addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                    log.info("[JobX] Netty upload file done!for request id:{},action:{}", request.getId(), request.getAction());
                                }
                            });
                            randomAccessFile.close();
                        }
                    }catch (Exception e) {
                        throw new RpcException(e);
                    }
                }
            }
        });

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("[JobX] agent channelInactive");
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cause.printStackTrace();
        ctx.close();
    }

}