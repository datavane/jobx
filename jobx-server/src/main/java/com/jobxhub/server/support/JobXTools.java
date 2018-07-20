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

package com.jobxhub.server.support;


import com.jobxhub.common.Constants;
import com.jobxhub.common.util.*;
import com.jobxhub.server.session.cached.CachedManager;
import com.jobxhub.server.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import com.jobxhub.common.util.collection.HashMap;

import static com.jobxhub.common.util.CommonUtils.uuid;

public final class JobXTools {

    private static String resourceId;

    public static final String SERVER_ID = uuid();

    private static Logger logger = LoggerFactory.getLogger(JobXTools.class);

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
                cachedContext = new ClassPathXmlApplicationContext("classpath*:".concat(conf));
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
        session.removeAttribute(Constants.PARAM_XSRF_NAME_KEY);
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
            token = (String) session.getAttribute(Constants.PARAM_XSRF_NAME_KEY);
            if (null == token) {
                token = CommonUtils.uuid();
                session.setAttribute(Constants.PARAM_XSRF_NAME_KEY, token);
                CookieUtils.setCookie(response, Constants.PARAM_XSRF_NAME_KEY, token, -1, request.getServerName());
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

    public static class Auth {
        public static String publicKey = null;
        public static String privateKey = null;
        public static String KEY_PATH = null;
        public static String PRIVATE_KEY_PATH = null;
        public static String PUBLIC_KEY_PATH = null;

        private static void generateKey() {
            if (CommonUtils.isEmpty(publicKey, privateKey)) {
                try {
                    File keyPath = new File(KEY_PATH);
                    if (!keyPath.exists()) {
                        keyPath.mkdirs();
                    }
                    Map<String, Object> keyMap = RSAUtils.genKeyPair();
                    publicKey = RSAUtils.getPublicKey(keyMap);
                    privateKey = RSAUtils.getPrivateKey(keyMap);
                    File pubFile = new File(getPublicKeyPath());
                    File prvFile = new File(getPrivateKeyPath());
                    IOUtils.writeText(pubFile, publicKey, Constants.CHARSET_UTF8);
                    IOUtils.writeText(prvFile, privateKey, Constants.CHARSET_UTF8);
                } catch (Exception e) {
                    logger.error("[JobX] error:{}" + e.getMessage());
                    throw new RuntimeException("init RSA'publicKey and privateKey error!");
                }
            }
        }

        public static String getPublicKey() {
            return getKey(KeyType.PUBLIC);
        }

        public static String getPrivateKey() {
            return getKey(KeyType.PRIVATE);
        }

        public static String getKey(KeyType type) {
            File file = new File(type.equals(KeyType.PUBLIC) ? getPublicKeyPath() : getPrivateKeyPath());
            if (file.exists()) {
                switch (type) {
                    case PUBLIC:
                        publicKey = IOUtils.readText(file, Constants.CHARSET_UTF8);
                        if (CommonUtils.isEmpty(publicKey)) {
                            generateKey();
                        }
                        break;
                    case PRIVATE:
                        privateKey = IOUtils.readText(file, Constants.CHARSET_UTF8);
                        if (CommonUtils.isEmpty(privateKey)) {
                            generateKey();
                        }
                        break;
                }
            } else {
                generateKey();
            }
            return type.equals(KeyType.PUBLIC) ? publicKey : privateKey;
        }

        public static String getKeyPath() {
            if (KEY_PATH == null) {
                KEY_PATH = Constants.JOBX_USER_HOME;
                // 从config.properties配置都读取用户手动设置的keypath的位置,配置文件里默认没有,不建议用户指定
                // 如果指定了位置可能会导致之前所有已可ssh登录的机器无法登陆,需要再次输入用户名密码
                String path = PropertyPlaceholder.get(Constants.PARAM_JOBX_KEYPATH_KEY);
                if (path != null) {
                    KEY_PATH = path;
                }
            }
            return KEY_PATH;
        }

        private static String getPrivateKeyPath() {
            PRIVATE_KEY_PATH = getKeyPath() + File.separator + "id_rsa";
            return PRIVATE_KEY_PATH;
        }

        private static String getPublicKeyPath() {
            PUBLIC_KEY_PATH = getPrivateKeyPath() + ".pub";
            return PUBLIC_KEY_PATH;
        }

        enum KeyType {
            PUBLIC, PRIVATE
        }

    }

}



