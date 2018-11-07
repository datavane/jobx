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
package com.jobxhub.service.session.wrapper;

import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;


@SuppressWarnings("deprecation")
public class HttpSessionWrapper implements HttpSession {
    private HttpSession delegate;

    public HttpSessionWrapper(HttpSession session) {
        super();
        this.delegate = session;
    }

    @Override
    public Object getAttribute(String key) {
        return delegate.getAttribute(key);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return delegate.getAttributeNames();
    }

    @Override
    public long getCreationTime() {
        return delegate.getCreationTime();
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return delegate.getLastAccessedTime();
    }

    @Override
    public int getMaxInactiveInterval() {
        return delegate.getMaxInactiveInterval();
    }

    @Override
    public ServletContext getServletContext() {
        return delegate.getServletContext();
    }

    @Override
    @Deprecated
    public HttpSessionContext getSessionContext() {
        return delegate.getSessionContext();
    }

    @Override
    public Object getValue(String key) {
        return getAttribute(key);
    }

    @Override
    public String[] getValueNames() {
        return (String[]) Collections.list(getAttributeNames()).toArray(new String[]{});
    }

    @Override
    public void invalidate() {
        delegate.invalidate();
    }

    @Override
    public boolean isNew() {
        return delegate.isNew();
    }

    @Override
    public void putValue(String key, Object value) {
        setAttribute(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        delegate.removeAttribute(key);
    }

    @Override
    public void removeValue(String key) {
        removeAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        delegate.setAttribute(key, value);
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        delegate.setMaxInactiveInterval(interval);
    }

}
