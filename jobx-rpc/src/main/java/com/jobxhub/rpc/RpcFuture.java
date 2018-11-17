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

package com.jobxhub.rpc;

import com.jobxhub.common.Constants;
import com.jobxhub.common.job.Request;
import com.jobxhub.common.job.Response;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class RpcFuture {


    public static final Map<Long, RpcFuture> futures = new ConcurrentHashMap<Long, RpcFuture>();

    public static Boolean scanClean = false;

    private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();
    private final Long futureId;
    private volatile Request request;
    private volatile Response response;
    private volatile Long startTime;
    private volatile Integer timeout;
    private volatile InvokeCallback invokeCallback;

    public RpcFuture(Request request) {
        this.request = request;
        this.timeout = this.request.getMillisTimeOut();
        this.startTime = System.currentTimeMillis();
        this.futureId = request.getId();
        futures.put(this.futureId, this);
        if (!scanClean) {
            scanClean = true;
            this.scanCleanTimeOut();
        }
    }



    public RpcFuture(Request request,InvokeCallback invokeCallback) {
        this(request);
        this.invokeCallback = invokeCallback;
    }

    public boolean isDone() {
        return response != null;
    }

    public Response get() throws TimeoutException {
        return get(this.timeout, TimeUnit.MILLISECONDS);
    }

    public Response get(long timeout, TimeUnit unit) throws TimeoutException {
        if (!isDone()) {
            lock.lock();
            try {
                while (!isDone()) {
                    done.await(timeout, unit);
                    if (isDone() || System.currentTimeMillis() - this.startTime > timeout) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
            if (!isDone()) {
                throw getTimeoutException();
            }
        }
        return this.response;
    }

    public static void received(Response response) {
        try {
            RpcFuture future = futures.remove(response.getId());
            if (future != null) {
                future.doReceived(response);
            } else {
                log.warn("[JobX]The timeout response finally returned at "
                        + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()))
                        + ", response " + future.getRequest().getAddress());
            }
        } finally {
            futures.remove(response.getId());
        }
    }

    private void doReceived(Response response) {
        lock.lock();
        try {
            this.response = response;
            long useTime = System.currentTimeMillis() - startTime;
            if (useTime > this.timeout) {
                log.warn("[JobX]Service response time is too slow. Request id:{}. Response Time:{}", this.futureId, useTime);
            }
            if (done != null) {
                done.signal();
            }
            this.invokeCallback();

        } finally {
            lock.unlock();
        }
    }

    public void caught(Throwable throwable) {
        lock.lock();
        try {
            if (!(throwable instanceof IOException) && !(throwable instanceof SecurityException)) {
                throwable = new IOException(throwable);
            }
            this.response = Response.response(request);
            this.response.setThrowable(throwable);
            this.response.setStartTime(this.startTime);
            this.response.setSuccess(false);
            this.response.setExitCode(Constants.ExitCode.ERROR_EXEC.getValue());
            invokeCallback();
        } finally {
            lock.unlock();
        }
    }

    private void invokeCallback() {

        if (this.invokeCallback == null) {
            return;
        }

        log.info("[JobX] async callback invoke");

        if (this.response == null) {
            throw new IllegalStateException("[JobX]response cannot be null. host:" + this.request.getAddress() + ",action: " + this.request.getAction());
        }

        if (this.response.getThrowable() == null) {
            try {
                invokeCallback.done(this.response);
            } catch (Exception e) {
                log.error("[JobX]callback done invoke error .host:{},action:{}:,caught:{}", this.request.getAddress(), this.request.getAction(), e);
            }
        } else {
            try {
                invokeCallback.caught(response.getThrowable());
            } catch (Exception e) {
                log.error("[JobX]callback caught invoke error .host:{},action:{}:,caught:{}", this.request.getAddress(), this.request.getAction(), e);
            }
        }
    }

    public TimeoutException getTimeoutException() {
        return new TimeoutException("[JobX] RPC timeout! host:" + request.getAddress() + ",action:" + request.getAction());
    }

    private void scanCleanTimeOut() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        for (RpcFuture future : futures.values()) {
                            if (future == null || future.isDone()) {
                                continue;
                            }
                            if (System.currentTimeMillis() - future.getStartTime() > future.getTimeout()) {
                                RpcFuture.this.caught(getTimeoutException());
                            }
                        }
                        Thread.sleep(30);
                    } catch (Throwable e) {
                        log.error("Exception when scan the timeout invocation of remoting.", e);
                    }
                }
            }
        }, "JobXRpcTimeoutScanTimer");
        thread.setDaemon(true);
        thread.start();
    }

    public Long getFutureId() {
        return futureId;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
