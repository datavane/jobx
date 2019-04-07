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

package com.jobxhub.service.support;


import com.jobxhub.common.Constants;
import com.jobxhub.common.util.*;
import com.jobxhub.service.session.cached.CachedManager;
import com.jobxhub.service.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import com.jobxhub.common.util.collection.HashMap;

import static com.jobxhub.common.util.CommonUtils.uuid;

@Slf4j
public final class JobXTools {

    private static String resourceId;

    public static final String SERVER_ID = uuid();

    private static ApplicationContext cachedContext;

    static {
        if (Constants.JOBX_CLUSTER) {
            Constants.CachedProvider provider = Constants.CachedProvider.getByName(Constants.JOBX_CACHED);
            if (provider == null) {
                throw new ExceptionInInitializerError("[JobX] please check parameter 'jobx.cached' value,must be 'redis' or 'memcached'");
            }
            String conf = String.format(Constants.SESSION_CONF_FORMAT,provider.getName());
            try {
                Enumeration<URL> urls = JobXTools.class.getClassLoader().getResources(conf);
                if (urls == null || !urls.hasMoreElements()) {
                    throw new ExceptionInInitializerError("[JobX] cache conf file ["+conf+"] not found!");
                }
                cachedContext = new ClassPathXmlApplicationContext("src/main/java/com/jobxhub/service/support/JobXTools.java".concat(conf));
            } catch (IOException e) {
                throw new ExceptionInInitializerError("[JobX] init cache conf error:"+ e );
            }
        }
    }

    public static CachedManager getCachedManager() {
        return cachedContext.getBean(CachedManager.class);
    }

    public static boolean isPermission(HttpSession session) {
        Object obj = session.getAttribute(Constants.PARAM_PERMISSION_KEY);
        if (obj == null) {
            return false;
        }
        return (Boolean) obj;
    }

    public static void logined(HttpServletRequest request, User user) {
        HttpSession session = request.getSession();
        session.setAttribute(Constants.PARAM_HTTP_SESSION_ID_KEY, session.getId());
        session.setAttribute(Constants.PARAM_LOGIN_USER_KEY, user);
        session.setAttribute(Constants.PARAM_LOGIN_USER_ID_KEY, user.getUserId());
        session.setAttribute(Constants.PARAM_CONTEXT_PATH_NAME_KEY, WebUtils.getWebUrlPath(request));
    }

    public static User getUser(HttpSession session) {
        return (User) session.getAttribute(Constants.PARAM_LOGIN_USER_KEY);
    }

    public static Long getUserId(HttpSession session) {
        return (Long) session.getAttribute(Constants.PARAM_LOGIN_USER_ID_KEY);
    }

    public static void invalidSession(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        session.removeAttribute(Constants.PARAM_LOGIN_USER_KEY);
        session.removeAttribute(Constants.PARAM_LOGIN_USER_ID_KEY);
        session.removeAttribute(Constants.PARAM_PERMISSION_KEY);
        session.removeAttribute(Constants.PARAM_HTTP_SESSION_ID_KEY);
        session.removeAttribute(Constants.PARAM_TERMINAL_TOKEN_KEY);
        session.removeAttribute(Constants.PARAM_ACCESS_TOKEN_KEY);
        TerminalSession.exit(request);
        session.removeAttribute(Constants.PARAM_LOGIN_MSG_KEY);
        session.removeAttribute(Constants.PARAM_CONTEXT_PATH_NAME_KEY);
        session.invalidate();
    }

    public static String getResourceId() {
        if (resourceId == null) {
            resourceId = CommonUtils.uuid();
        }
        return resourceId;
    }

    public static String generateXSRF(HttpServletRequest request, HttpServletResponse response) {
        String token;
        HttpSession session = request.getSession();
        synchronized (session) {
            token = (String) session.getAttribute(Constants.PARAM_ACCESS_TOKEN_KEY);
            if (null == token) {
                token = CommonUtils.uuid();
                session.setAttribute(Constants.PARAM_ACCESS_TOKEN_KEY, token);
                CookieUtils.setCookie(response, Constants.PARAM_ACCESS_TOKEN_KEY, token, -1, request.getServerName());
            }
        }
        return token;
    }

    public static class CACHE {

        private static Map<String, Object> cache = new HashMap<String, Object>();

        public static Object get(String key) {
            return cache.get(key);
        }

        public static <T> T get(String key, Class<T> clazz) {
            return (T) cache.get(key);
        }

        public static void put(String key, Object value) {
            cache.put(key, value);
        }

        public static Object remove(String key) {
            return cache.remove(key);
        }
    }



}



