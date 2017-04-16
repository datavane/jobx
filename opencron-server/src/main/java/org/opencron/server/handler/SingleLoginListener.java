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
 *
 *
 */

package org.opencron.server.handler;

import org.opencron.server.domain.User;
import org.opencron.server.job.OpencronTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created by benjobs on 2017/1/12.
 */
public class SingleLoginListener implements HttpSessionListener {

    // key为sessionId，value为HttpSession
    private static Map<String, HttpSession> singleLoginSessionMap = new ConcurrentHashMap<String, HttpSession>(500);

    /**
     * HttpSessionListener中的方法，在创建session
     */
    public void sessionCreated(HttpSessionEvent event) {
    }

    /**
     * HttpSessionListener中的方法，回收session时,删除sessionMap中对应的session
     */
    public void sessionDestroyed(HttpSessionEvent event) {
        getSingleLoginSessionMap().remove(event.getSession().getId());
    }

    /**
     * 得到在线用户会话集合
     */
    public static List<HttpSession> getUserSessions() {
        List<HttpSession> list = new ArrayList<HttpSession>();
        for(Map.Entry<String,HttpSession> entry: singleLoginSessionMap.entrySet()) {
            HttpSession session = entry.getValue();
            list.add(session);
        }
        return list;
    }

    /**
     * 得到用户对应会话map，key为用户ID,value为会话ID
     */
    public static Map<Long, String> getSessionIds() {
        Map<Long, String> map = new HashMap<Long, String>();
        for(Map.Entry<String,HttpSession> entry: singleLoginSessionMap.entrySet()){
            String sessionId = entry.getKey();
            HttpSession session = entry.getValue();
            User user = (User) session.getAttribute(OpencronTools.LOGIN_USER);
            if (user != null) {
                map.put(user.getUserId(), sessionId);
            }
        }
        return map;
    }

    /**
     * 移除用户Session
     */
    public synchronized static void removeUserSession(Long userId) {
        Map<Long, String> userSessionMap = getSessionIds();
        if (userSessionMap.containsKey(userId)) {
            String sessionId = userSessionMap.get(userId);
            HttpSession httpSession = singleLoginSessionMap.get(sessionId);
            if (!httpSession.isNew()) {
                httpSession.removeAttribute(OpencronTools.LOGIN_USER);
                //httpSession.invalidate();
            }
            singleLoginSessionMap.remove(sessionId);
        }
    }

    /**
     * 增加用户到session集合中
     */
    public static void addUserSession(HttpSession session) {
        getSingleLoginSessionMap().put(session.getId(), session);
    }

    /**
     * 移除一个session
     */
    public static void removeSession(String sessionID) {
        getSingleLoginSessionMap().remove(sessionID);
    }

    public static boolean containsKey(String key) {
        return getSingleLoginSessionMap().containsKey(key);
    }

    public synchronized static boolean logined(User user) {
        for(Map.Entry<String,HttpSession> entry: singleLoginSessionMap.entrySet()){
            HttpSession session = entry.getValue();
            User sessionuser = (User) session.getAttribute(OpencronTools.LOGIN_USER);
            if (sessionuser != null) {
                if (sessionuser.getUserId().equals(user.getUserId())){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取在线的sessionMap
     */
    public static Map<String, HttpSession> getSingleLoginSessionMap() {
        return singleLoginSessionMap;
    }

    public static HttpSession getLoginedSession(Long userId) {
        String sessionId = getSessionIds().get(userId);
        if (sessionId!=null) {
           return getSingleLoginSessionMap().get(sessionId);
        }
        return null;
    }
}