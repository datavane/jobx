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
package org.opencron.common.rpc.model;

import org.opencron.common.rpc.core.IdGenerator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Request implements Serializable {
    private String id;
    public String hostName;
    public int port;
    /**
     * @see Action
     */
    public Action action;
    public String password;
    public Map<String, String> params;


    public String getId() {
        return id;
    }

    public Request setId(String id) {
        this.id = id;
        return this;
    }

    public String getHostName() {
        return hostName;
    }

    public Request setHostName(String hostName) {
        this.hostName = hostName;
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

    public String getPassword() {
        return password;
    }

    public Request setPassword(String password) {
        this.password = password;
        return this;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Request setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public static Request request(String hostName, Integer port, Action action, String password) {
        return new Request().setId(hostName+":"+port).setHostName(hostName).setPort(port).setAction(action).setPassword(password);
    }

    public Request putParam(String key, String value) {
        if (this.params == null) {
            this.params = new HashMap<String, String>(0);
        }
        this.params.put(key, value);
        return this;
    }

}
