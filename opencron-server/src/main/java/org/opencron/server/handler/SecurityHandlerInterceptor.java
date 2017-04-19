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


package org.opencron.server.handler;


import org.opencron.common.utils.CommonUtils;
import org.opencron.common.utils.StringUtils;
import org.opencron.common.utils.WebUtils;
import org.opencron.server.domain.User;
import org.opencron.server.job.OpencronTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * 登陆权限拦截器
 */
@Component
public class SecurityHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityHandlerInterceptor.class);

    public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {

        request = new XssHttpServletRequest(request);

        HttpSession session = request.getSession();

        String requestURI = request.getContextPath() + request.getServletPath();

        //
        // 考虑到以后升级可能会改css或者js,用户重新升级部署后肯能有缓存,导致项目失败,
        // 特此加上防止用户端有缓存的Id来防止资源缓存,每次项目启动会生成一个随机码添加到所有的资源引用后
        //
        session.setAttribute("resourceId",OpencronTools.getResourceId());

        //静态资源,页面
        if ( requestURI.contains("/css/")
                || requestURI.contains("/fonts/")
                || requestURI.contains("/img/")
                || requestURI.contains("/js/")
                || requestURI.contains("/WEB-INF") ) {
            return super.preHandle(request, response, handler);
        }

        //登陆
        if (requestURI.contains("/login")||requestURI.contains("/upload")) {
            return super.preHandle(request, response, handler);
        }

        String referer = request.getHeader("referer");
        if (referer != null && !referer.startsWith(WebUtils.getWebUrlPath(request))) {
            response.sendRedirect("/");
            logger.info("[opencron]Bad request,redirect to login page");
            OpencronTools.invalidSession(session);
            return false;
        }

        try {
            User user = OpencronTools.getUser(session);
            if (user == null) {
                //跳到登陆页面
                response.sendRedirect("/");
                logger.info("[opencron]User not login,redirect to login page");
                return false;
            }
        }catch (IllegalStateException e) {
            logger.info("[opencron]Session already invalidated,redirect to login page");
            response.sendRedirect("/");
            return false;
        }

        //普通管理员不可访问的资源
        if (!OpencronTools.isPermission(session) &&
                (requestURI.contains("/config/")
                        || requestURI.contains("/user/view")
                        || requestURI.contains("/user/add")
                        || requestURI.contains("/agent/add")
                        || requestURI.contains("/agent/edit"))) {
            logger.info("[opencron]illegal or limited access");
            return false;
        }

        if (handler instanceof HandlerMethod) {
            if (!verifyCSRF(request)) {
                response.sendRedirect("/");
                logger.info("[opencron]Bad request,redirect to login page");
                OpencronTools.invalidSession(session);
                return false;
            }
        }

        return super.preHandle(request, response, handler);
    }

    private boolean verifyCSRF(HttpServletRequest request) {

        String requstCSRF = OpencronTools.getCSRF(request);
        if (CommonUtils.isEmpty(requstCSRF)) {
            return false;
        }
        String sessionCSRF = OpencronTools.getCSRF(request.getSession());
        if (CommonUtils.isEmpty(sessionCSRF)) {
            return false;
        }
        return requstCSRF.equals(sessionCSRF);
    }


    class XssHttpServletRequest extends HttpServletRequestWrapper {

        public XssHttpServletRequest(HttpServletRequest servletRequest) {
            super(servletRequest);
        }
        public String[] getParameterValues(String parameter) {
            String[] values = super.getParameterValues(parameter);
            if (values==null)  {
                return null;
            }
            int count = values.length;
            String[] encodedValues = new String[count];
            for (int i = 0; i < count; i++) {
                encodedValues[i] = cleanXSS(values[i]);
            }
            return encodedValues;
        }

        public Map getParameterMap() {
            Map<String, String[]> map = super.getParameterMap();
            for(Map.Entry<String,String[]> entry:map.entrySet()){
                String[] values = entry.getValue();
                for(int i=0;i<values.length;i++){
                    values[i] = cleanXSS(values[i]);
                }
                map.put(entry.getKey(),values);
            }
            return map;
        }

        public Enumeration getParameterNames() {

           class MyEnumeration implements Enumeration {
                private int count;
                private int length;
                private Object[] data;
                MyEnumeration(Object[] data) {
                    this.count = 0;
                    this.length = data.length;
                    this.data = data;
                }
                @Override
                public boolean hasMoreElements() {
                    return (count< length);
                }
                @Override
                public Object nextElement() {
                    return data[count++];
                }
            }
            Enumeration enumeration = super.getParameterNames();
            List<Object> list = new ArrayList<Object>();
            while (enumeration.hasMoreElements()) {
                Object value = enumeration.nextElement();
                value = cleanXSS((String)value);
                list.add(value);
            }
            return new MyEnumeration(list.toArray());
        }

        public String getParameter(String parameter) {
            String value = super.getParameter(parameter);
            if (value == null) {
                return null;
            }
            return cleanXSS(value);
        }

        private String cleanXSS(String value) {
            if (value==null) return null;
            return StringUtils.htmlEncode(value);
        }
    }


}
