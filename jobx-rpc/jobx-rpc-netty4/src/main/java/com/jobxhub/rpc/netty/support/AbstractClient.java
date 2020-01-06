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
package com.jobxhub.rpc.netty.support;

import com.jobxhub.common.Constants;
import com.jobxhub.common.job.Request;
import com.jobxhub.common.util.HttpUtils;
import com.jobxhub.common.util.collection.HashMap;
import com.jobxhub.rpc.Client;
import com.jobxhub.rpc.RpcFuture;
import com.jobxhub.rpc.netty.NettyChannelWrapper;
import com.jobxhub.rpc.support.ChannelWrapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author benjobs
 */
public abstract class AbstractClient implements Client {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected Bootstrap bootstrap;

	private final Lock connectLock = new ReentrantLock();

	protected final ConcurrentHashMap<String, ChannelWrapper> channelTable = new ConcurrentHashMap<String, ChannelWrapper>();

	public final Map<Long, RpcFuture> futureTable = new HashMap<Long, RpcFuture>(256);

	public Channel getChannel(Request request) {
		connectLock.lock();
		try {
			NettyChannelWrapper nettyChannelWrapper = (NettyChannelWrapper) this.channelTable.get(request.getAddress());
			if (nettyChannelWrapper != null && nettyChannelWrapper.isActive()) {
				return nettyChannelWrapper.getChannel();
			}
			// 发起异步连接操作
			this.doConnect(request);
			ChannelFuture channelFuture = this.bootstrap.connect(HttpUtils.parseSocketAddress(request.getAddress()));
			nettyChannelWrapper = new NettyChannelWrapper(channelFuture);
			if (channelFuture.awaitUninterruptibly(Constants.RPC_TIMEOUT)) {
				if (nettyChannelWrapper.isActive()) {
					if (logger.isInfoEnabled()) {
						logger.info("[JobX] NettyRPC getChannel: connect remote host[{}] success, {}", request.getAddress(), channelFuture.toString());
					}
					this.channelTable.put(request.getAddress(), nettyChannelWrapper);
					return nettyChannelWrapper.getChannel();
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("[JobX] NettyRPC getChannel: connect remote host[" + request.getAddress() + "] failed, " + channelFuture.toString(), channelFuture.cause());
					}
				}
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn("[JobX] NettyRPC getChannel: connect remote host[{}] timeout {}ms, {}", request.getAddress(), Constants.RPC_TIMEOUT, channelFuture);
				}
			}
		} finally {
			connectLock.unlock();
		}
		return null;
	}

	private void doConnect(Request request) {
		if (this.bootstrap == null) {
			this.connect(request);
		}
	}

	@Override
	public abstract void connect(Request request);

	@Override
	public void disconnect() throws Throwable {
		connectLock.lock();
		try {
			for (Map.Entry<String, ChannelWrapper> entry : channelTable.entrySet()) {
				ChannelWrapper channelWrapper = entry.getValue();
				if (channelWrapper != null) {
					channelWrapper.close();
				}
			}
		} finally {
			connectLock.unlock();
		}
	}

	public RpcFuture getRpcFuture(Long id) {
		return this.futureTable.get(id);
	}

	public class FutureListener implements ChannelFutureListener {

		private RpcFuture rpcFuture;

		public FutureListener(RpcFuture rpcFuture) {
			if (rpcFuture != null) {
				this.rpcFuture = rpcFuture;
				futureTable.put(rpcFuture.getFutureId(), rpcFuture);
			}
		}

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			if (future.isSuccess()) {
				if (logger.isInfoEnabled()) {
					logger.info("[JobX] NettyRPC sent success, request id:{}", rpcFuture.getRequest().getId());
				}
				return;
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("[JobX] NettyRPC sent failure, request id:{}", rpcFuture.getRequest().getId());
				}
				if (this.rpcFuture != null) {
					rpcFuture.caught(future.cause());
				}
			}
			futureTable.remove(rpcFuture.getFutureId());
		}
	}
}
