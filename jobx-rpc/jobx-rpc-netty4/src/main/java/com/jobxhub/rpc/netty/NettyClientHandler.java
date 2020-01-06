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
import com.jobxhub.common.job.*;
import com.jobxhub.common.logging.LoggerFactory;
import com.jobxhub.common.util.CommonUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;

import java.io.RandomAccessFile;

/**
 * @author benjobs
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<Response> {

    private Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    private NettyClient nettyClient;

    private Request request;

    private int byteRead;

    public RandomAccessFile randomAccessFile;

    private RequestFile requestFile;

    public NettyClientHandler(NettyClient nettyClient, Request request) {
        this.nettyClient = nettyClient;
        this.request = request;
    }

    @Override
    public void channelActive(ChannelHandlerContext handlerContext) throws Exception {
        if (request.getAction().equals(Action.UPLOAD)) {
            try {
                requestFile = request.getUploadFile();
                randomAccessFile = new RandomAccessFile(requestFile.getFile(), "r");
                requestFile.setFileSize(randomAccessFile.length());
                randomAccessFile.seek(requestFile.getStarPos());
                byte[] bytes = new byte[requestFile.getReadBuffer()];
                if ((byteRead = randomAccessFile.read(bytes)) != -1) {
                    requestFile.setEndPos(byteRead);
                    requestFile.setBytes(bytes);
                    request.setUploadFile(requestFile);
                    handlerContext.writeAndFlush(request);
                    requestFile.setBytes(null);
                    requestFile.setEndPos(-1);
                    logger.info("[JobX] NettyRPC file upload，readLength starting... request id:{}", request.getId());
                } else {
                    logger.info("[JobX] NettyRPC file upload，readLength done! request id:{}", request.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext handlerContext, Response response) throws Exception {
        if (!response.getAction().equals(Action.UPLOAD)) {
            if (logger.isInfoEnabled()) {
                logger.info("[JobX] nettyRPC client receive response id:{}", response.getId());
            }
            nettyClient.getRpcFuture(response.getId()).received(response);
            return;
        }
        ResponseFile responseFile = response.getUploadFile();
        if (responseFile.isEnd()) {
            randomAccessFile.close();
            response.setUploadFile(responseFile);
            //有后续动作...
            if (CommonUtils.notEmpty(requestFile.getPostCmd())) {
                //replace $1
                String postCmd = requestFile.getPostCmd().replaceAll("[^\\s+]+\\$1|\\$1", request.getUploadFile().getFile().getName());
                String cd = "cd " + requestFile.getSavePath();
                //cd savePath...
                if (!requestFile.getPostCmd().startsWith(cd)) {
                    requestFile.setPostCmd(cd + " && " + postCmd);
                }
                request.setAction(Action.EXECUTE);
                request.putParam(Constants.PARAM_COMMAND_KEY, requestFile.getPostCmd())
                        .putParam(Constants.PARAM_PID_KEY, requestFile.getFileMD5())
                        .putParam(Constants.PARAM_TIMEOUT_KEY, Constants.RPC_TIMEOUT + "")
                        .putParam(Constants.PARAM_SUCCESSEXIT_KEY, "1");
                handlerContext.writeAndFlush(request);
            } else {
                nettyClient.getRpcFuture(response.getId()).received(response);
            }
        } else {
            long start = responseFile.getStart();
            if (start != -1) {
                randomAccessFile = new RandomAccessFile(requestFile.getFile(), "r");
                randomAccessFile.seek(start);
                int needSize = (int) (requestFile.getFileSize() - start);
                int sendLength = responseFile.getReadBuffer();
                if (needSize < sendLength) {
                    sendLength = needSize;
                }
                byte[] bytes = new byte[sendLength];
                if (needSize > 0 && (byteRead = randomAccessFile.read(bytes)) != -1) {
                    try {
                        requestFile.setEndPos(byteRead);
                        requestFile.setBytes(bytes);
                        request.setUploadFile(requestFile);
                        handlerContext.writeAndFlush(request);
                        requestFile.setBytes(null);
                        requestFile.setEndPos(-1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    randomAccessFile.close();
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        if (logger.isErrorEnabled()) {
            logger.error("[jobx nettyRPC error,cause{}]", cause);
        }
    }
}
