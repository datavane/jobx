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
package com.jobxhub.core.session.wrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.jobxhub.core.session.HttpSessionStore;


@SuppressWarnings("unchecked")
public class HttpSessionStoreWrapper extends HttpSessionWrapper {

    private HttpSessionStore store;

    private String sessionId;

    private Map sessionData;

    public HttpSessionStoreWrapper(HttpSessionStore store, HttpSession session, String sessionId, Map sessionData) {
        super(session);
        this.sessionId = sessionId;
        this.sessionData = sessionData;
        this.store = store;
    }

    @Override
    public void invalidate() {
        sessionData.clear();
        store.deleteSession(getId());
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public Object getAttribute(String key) {
        return this.sessionData.get(key);
    }

    @Override
    public Enumeration getAttributeNames() {
        return Collections.enumeration(sessionData.keySet());
    }

    @Override
    public void removeAttribute(String key) {
        sessionData.remove(key);
        store.setSession(sessionId, sessionData);
    }

    @Override
    public void setAttribute(String key, Object value) {
        sessionData.put(key, value);
        store.setSession(sessionId, sessionData);
    }

}
