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


package com.jobxhub.server.handler;


import com.alibaba.fastjson.JSON;
import com.jobxhub.common.Constants;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.CookieUtils;
import com.jobxhub.common.util.collection.HashMap;
import com.jobxhub.server.annotation.RequestRepeat;
import com.jobxhub.server.support.JobXTools;
import com.jobxhub.server.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.*;
import java.io.Serializable;

/**
 * 登陆权限拦截器
 */
@Component
public class SecurityHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityHandlerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        request.setAttribute("uri", request.getRequestURI());

        HttpSession session = request.getSession();

        if (session.getAttribute(Constants.PARAM_SKIN_NAME_KEY) == null) {
            Cookie cookie = CookieUtils.getCookie(request, Constants.PARAM_SKIN_NAME_KEY);
            if (cookie != null) {
                String skin = cookie.getValue();
                session.setAttribute(Constants.PARAM_SKIN_NAME_KEY, skin);
            }
        }

        String requestURI = request.getContextPath() + request.getServletPath();

        //
        // 考虑到以后升级可能会改css或者js,用户重新升级部署后肯能有缓存,导致项目失败,
        // 特此加上防止用户端有缓存的Id来防止资源缓存,每次项目启动会生成一个随机码添加到所有的资源引用后
        //
        session.setAttribute("resourceId", JobXTools.getResourceId());

        //静态资源,页面
        if (requestURI.equals("/")
                || requestURI.contains("/static/")
                || requestURI.contains("/WEB-INF")
                || requestURI.contains("/login")
                || requestURI.contains("/api")
                || requestURI.contains("/upload")) {

            return super.preHandle(request, response, handler);
        }

        String port = request.getServerPort() == 80 ? "" : (":" + request.getServerPort());
        String path = request.getContextPath().replaceAll("/$", "");
        String urlPath = request.getScheme() + "://" + request.getServerName() + port + path;

        String referer = request.getHeader("referer");
        if (referer != null && !referer.startsWith(urlPath)) {
            response.sendRedirect("/");
            if (logger.isInfoEnabled()) {
                logger.info("[JobX]Bad request,redirect to login page");
            }
            JobXTools.invalidSession(request);
            return false;
        }

        try {
            User user = JobXTools.getUser(session);
            if (user == null) {
                //跳到登陆页面
                response.sendRedirect("/");
                if (logger.isInfoEnabled()) {
                    logger.info("[JobX]User not login,redirect to login page");
                }
                return false;
            }
        } catch (IllegalStateException e) {
            if (logger.isInfoEnabled()) {
                logger.info("[JobX]Session already invalidated,redirect to login page");
            }
            response.sendRedirect("/");
            return false;
        }

        //普通管理员不可访问的资源
        if (!JobXTools.isPermission(session) &&
                (requestURI.contains("/config/")
                        || requestURI.contains("/user/view")
                        || requestURI.contains("/user/add")
                        || requestURI.contains("/agent/add")
                        || requestURI.contains("/agent/edit"))) {
            if (logger.isInfoEnabled()) {
                logger.info("[JobX]illegal or limited access");
            }
            return false;
        }

        if (handler instanceof HandlerMethod) {
            if (!verifyCSRF(request)) {
                response.sendRedirect("/");
                if (logger.isInfoEnabled()) {
                    logger.info("[JobX]Bad request,redirect to login page");
                }
                JobXTools.invalidSession(request);
                return false;
            }
            if (verifyRepeat(request,(HandlerMethod)handler)) {
                RequestRepeat requestRepeat = ((HandlerMethod) handler).getMethodAnnotation(RequestRepeat.class);
                //需要显示页面...
                if (requestRepeat.view()) {
                    response.sendRedirect("/repeat");
                }
                return false;
            }
        }

        return super.preHandle(request, response, handler);
    }

    /**
     *
     * @param httpServletRequest
     * @return repeat true
     *         noRepeat false
     */
    private boolean verifyRepeat(HttpServletRequest httpServletRequest,HandlerMethod handlerMethod) {
        //针对post请求
        if (httpServletRequest.getMethod().equals(RequestMethod.POST.name())) {
            if (handlerMethod.hasMethodAnnotation(RequestRepeat.class)) {
                String params = JSON.toJSONString(httpServletRequest.getParameterMap());
                String url = httpServletRequest.getRequestURI();
                Long reqTime = System.currentTimeMillis();

                HashMap<String, Serializable> nowUrlParams = new HashMap<String, Serializable>(0);
                nowUrlParams.put("url",url);
                nowUrlParams.put("params",params);
                nowUrlParams.put("reqTime",reqTime);

                HashMap preUrlParams = (HashMap) httpServletRequest.getSession().getAttribute("repeatData");
                if (preUrlParams == null) {
                    httpServletRequest.getSession().setAttribute("repeatData", nowUrlParams);
                    return false;
                } else {
                    httpServletRequest.getSession().setAttribute("repeatData", nowUrlParams);
                    if ( preUrlParams.getString("url").equals(nowUrlParams.getString("url")) &&
                        preUrlParams.getString("params").equals(nowUrlParams.getString("params")) ) {
                        //判断两次提交数据的时长间隔.....
                        Long diffTime = nowUrlParams.getLong("reqTime") - preUrlParams.getLong("reqTime");
                        if (diffTime<500) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        }
        return false;
    }

    private boolean verifyCSRF(HttpServletRequest request) {

        String cookieCSRF = CookieUtils.getCookieValue(request, Constants.PARAM_CSRF_NAME_KEY);
        if (CommonUtils.isEmpty(cookieCSRF)) {
            return false;
        }
        String sessionCSRF = (String) request.getSession().getAttribute(Constants.PARAM_CSRF_NAME_KEY);
        if (CommonUtils.isEmpty(sessionCSRF)) {
            return false;
        }
        return cookieCSRF.equals(sessionCSRF);
    }


}
