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

package org.opencron.common.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public abstract class CookieUtils {

    @SuppressWarnings("unchecked")
    public static Map<String, Cookie> cookieToMap(Cookie[] cookies) {
        if (cookies == null || cookies.length == 0)
            return new HashMap(0);

        Map map = new HashMap(cookies.length * 2);
        for (Cookie c : cookies) {
            map.put(c.getName(), c);
        }
        return map;
    }

    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie c = getCookie(request, name);
        return c != null ? c.getValue() : null;
    }

    public static void setCookie(HttpServletResponse response, String name, String value, int expire, String domain) {
        Cookie c = new Cookie(name, value);
        c.setPath("/");
        c.setMaxAge(expire);
        if (domain != null) {
            c.setDomain(domain);
        }

        response.addCookie(c);
    }

    public static void setCookie(HttpServletResponse response, String name, String value, int expire) {
        setCookie(response, name, value, expire);
    }

    public static void setCookie(HttpServletResponse response, String name, String value) {
        setCookie(response, name, value, -1);
    }

}
