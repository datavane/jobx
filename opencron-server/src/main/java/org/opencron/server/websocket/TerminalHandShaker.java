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

package org.opencron.server.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

import static org.opencron.server.job.OpencronTools.HTTP_SESSION_ID;
import static org.opencron.server.job.OpencronTools.SSH_SESSION_ID;


public class TerminalHandShaker extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession();
            attributes.put("cols",servletRequest.getServletRequest().getParameter("cols"));
            attributes.put("rows",servletRequest.getServletRequest().getParameter("rows"));
            attributes.put("width",servletRequest.getServletRequest().getParameter("width"));
            attributes.put("height",servletRequest.getServletRequest().getParameter("height"));
            attributes.put(SSH_SESSION_ID,session.getAttribute(SSH_SESSION_ID));
            attributes.put(HTTP_SESSION_ID,session.getAttribute(HTTP_SESSION_ID));
        }
        return super.beforeHandshake(request,response,wsHandler,attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        super.afterHandshake(request,response,wsHandler,exception);
    }

}
