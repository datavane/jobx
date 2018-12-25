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
package com.jobxhub.common.job;

import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.collection.HashMap;
import com.jobxhub.common.util.IdGenerator;

import java.io.Serializable;

public class Request implements Serializable  {

    private RpcType rpcType = RpcType.ASYNC;
    private String host;
    private Long id;
    private Integer port;
    private String address;
    private Integer timeOut;//调用超时时间限制(分钟)
    private Action action;
    private String password;
    private Long proxyId;
    private RequestFile uploadFile;
    private HashMap<String, Object> params = new HashMap<String, Object>(0);

    public Request() {

    }

    public static Request request(String host, Integer port, Action action, String password, Integer timeOut, Long proxyId) {
        return new Request()
                .setHost(host)
                .setPort(port)
                .setAction(action)
                .setPassword(password)
                .setTimeOut(timeOut)
                .setProxyId(proxyId)
                .setId(IdGenerator.getId());
    }

    public Request putParam(String key, Object value) {
        if (this.params == null) {
            this.params = new HashMap<String, Object>(0);
        }
        this.params.put(key, value);
        return this;
    }

    public String getHost() {
        return host;
    }

    public Request setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public Request setPort(int port) {
        this.port = port;
        return this;
    }

    public Action getAction() {
        return action;
    }

    public Request setAction(Action action) {
        this.action = action;
        return this;
    }

    public Integer getTimeOut() {
        //如果timeOut未设置,则返回24小时(1440分钟)
        if ( timeOut == null || timeOut<0 ) {
            return 60 * 24;
        }
        return timeOut;
    }

    public Integer getMillisTimeOut() {
       return getTimeOut() * 60 * 1000;
    }

    public Request setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Request setPassword(String password) {
        this.password = password;
        return this;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public Request setParams(HashMap<String, Object> params) {
        this.params = params;
        return this;
    }

    public RpcType getRpcType() {
        return rpcType;
    }

    public Request setRpcType(RpcType rpcType) {
        this.rpcType = rpcType;
        return this;
    }

    public String getAddress() {
        if (CommonUtils.notEmpty(this.host, this.port)) {
            this.address = this.host + ":" + this.port;
        }
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public Request setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getProxyId() {
        return proxyId;
    }

    public Request setProxyId(Long proxyId) {
        this.proxyId = proxyId;
        return this;
    }

    public RequestFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(RequestFile uploadFile) {
        this.uploadFile = uploadFile;
    }
}
