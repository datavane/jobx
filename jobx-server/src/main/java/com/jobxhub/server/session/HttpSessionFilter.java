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
package com.jobxhub.server.session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import com.jobxhub.common.Constants;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.CookieUtils;
import com.jobxhub.server.session.wrapper.HttpServletRequestSessionWrapper;
import com.jobxhub.server.session.wrapper.HttpSessionStoreWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;


@SuppressWarnings("unchecked")
public class HttpSessionFilter extends OncePerRequestFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(HttpSessionFilter.class);

    private HttpSessionStore sessionStore;

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        if (!Constants.JOBX_CLUSTER) {
            chain.doFilter(request, response);
        } else {
            if (sessionStore == null) {
                sessionStore = new HttpSessionStore();
            }
            String requestURI = request.getContextPath() + request.getServletPath();

            //过滤静态资源
            if (requestURI.startsWith("/static/") || requestURI.startsWith("/favicon.ico")) {
                chain.doFilter(request, response);
                return;
            }

            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=utf-8");
            Cookie sessionIdCookie = getOrGenerateSessionId(request, response);
            String sessionId = sessionIdCookie.getValue();

            HttpSession rawSession = request.getSession();

            Map sessionData = loadSessionData(sessionId);
            try {
                HttpSession sessionWrapper = new HttpSessionStoreWrapper(sessionStore, rawSession, sessionId, sessionData);
                chain.doFilter(new HttpServletRequestSessionWrapper(request, sessionWrapper), response);
            } finally {
                sessionStore.setSession(sessionId, sessionData);
            }
        }
    }

    private Map loadSessionData(String sessionId) {
        Map sessionData = null;
        try {
            sessionData = sessionStore.getSession(sessionId);
        } catch (Exception e) {
            sessionData = new HashMap();
            logger.warn("load session data error,cause:" + e, e);
        }
        return sessionData;
    }

    private Cookie getOrGenerateSessionId(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Cookie> cookieMap = CookieUtils.cookieToMap(request.getCookies());
        Cookie sessionIdCookie = cookieMap.get(Constants.PARAM_COOKIE_NAME_KEY);
        if (sessionIdCookie == null || StringUtils.isEmpty(sessionIdCookie.getValue())) {
            sessionIdCookie = generateCookie(request, response);
        } else {
            sessionIdCookie.setMaxAge(request.getSession().getMaxInactiveInterval() * 60 * 60 * 1000);
        }
        return sessionIdCookie;
    }

    private Cookie generateCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie sessionIdCookie = new Cookie(Constants.PARAM_COOKIE_NAME_KEY, CommonUtils.uuid());
        String domain = request.getServerName();
        if (domain != null) {
            sessionIdCookie.setDomain(domain);
        }
        sessionIdCookie.setPath("/");
        sessionIdCookie.setMaxAge(request.getSession().getMaxInactiveInterval());
        response.addCookie(sessionIdCookie);
        return sessionIdCookie;
    }

}
