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
package com.jobxhub.rpc.mina.support;

import com.jobxhub.common.Constants;
import com.jobxhub.common.job.Request;
import com.jobxhub.common.util.HttpUtils;
import com.jobxhub.common.util.collection.HashMap;
import com.jobxhub.rpc.Client;
import com.jobxhub.rpc.RpcFuture;
import com.jobxhub.rpc.mina.MinaConnectWrapper;
import com.jobxhub.rpc.support.ChannelWrapper;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
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

	protected NioSocketConnector connector;

	private final Lock connectLock = new ReentrantLock();

	protected final ConcurrentHashMap<String, ChannelWrapper> channelTable = new ConcurrentHashMap<String, ChannelWrapper>();

	public final Map<Long, RpcFuture> futureTable = new HashMap<Long, RpcFuture>(256);

	public ConnectFuture getConnect(Request request) {
		connectLock.lock();
		try {
			MinaConnectWrapper minaConnectWrapper = (MinaConnectWrapper) this.channelTable.get(request.getAddress());
			if (minaConnectWrapper != null && minaConnectWrapper.isActive()) {
				return minaConnectWrapper.getConnectFuture();
			}
			this.doConnect(request);
			ConnectFuture connectFuture = connector.connect(HttpUtils.parseSocketAddress(request.getAddress()));
			minaConnectWrapper = new MinaConnectWrapper(connectFuture);
			if (connectFuture.awaitUninterruptibly(Constants.RPC_TIMEOUT)) {
				if (minaConnectWrapper.isActive()) {
					if (logger.isInfoEnabled()) {
						logger.info("[JobX] MinaRPC getConnect: connect remote host[{}] success, {}", request.getAddress(), connectFuture.toString());
					}
					this.channelTable.put(request.getAddress(), minaConnectWrapper);
					return connectFuture;
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("[JobX] MinaRPC getConnect: connect remote host[" + request.getAddress() + "] failed, " + connectFuture.toString(), connectFuture.getException());
					}
				}
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn("[JobX] MinaRPC getConnect: connect remote host[{}] timeout {}ms, {}", request.getAddress(), Constants.RPC_TIMEOUT, connectFuture);
				}
			}
		} finally {
			connectLock.unlock();
		}
		return null;
	}


	private void doConnect(Request request) {
//		if (this.bootstrap == null) {
//			this.connect(request);
//		}
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
}
