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

import com.jobxhub.common.util.DigestUtils;
import com.jobxhub.server.dto.Terminal;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.jobxhub.common.util.collection.HashMap;

import static com.jobxhub.common.util.CommonUtils.notEmpty;

public class TerminalSession implements Serializable {

    //key--->WebSocketSession value--->TerminalClient
    public static Map<WebSocketSession, TerminalClient> terminalSession = new HashMap<WebSocketSession, TerminalClient>();

    public static TerminalClient get(WebSocketSession key) {
        return terminalSession.get(key);
    }

    public static TerminalClient get(String key) {
        for (Map.Entry<WebSocketSession, TerminalClient> entry : terminalSession.entrySet()) {
            TerminalClient client = entry.getValue();
            if (client.getClientId().equals(key)) {
                return client;
            }
        }
        return null;
    }

    public static void put(WebSocketSession key, TerminalClient terminalClient) {
        terminalSession.put(key, terminalClient);
    }

    public static TerminalClient remove(WebSocketSession key) {
        return terminalSession.remove(key);
    }

    public static boolean isOpened(Terminal terminal) {
        for (Map.Entry<WebSocketSession, TerminalClient> entry : terminalSession.entrySet()) {
            if (entry.getValue().getTerminal().equals(terminal)) {
                return true;
            }
        }
        return false;
    }

    public static List<TerminalClient> findClient(Serializable sessionId) throws IOException {
        List<TerminalClient> terminalClients = new ArrayList<TerminalClient>(0);
        if (notEmpty(terminalSession)) {
            for (Map.Entry<WebSocketSession, TerminalClient> entry : terminalSession.entrySet()) {
                TerminalClient terminalClient = entry.getValue();
                if (terminalClient != null && terminalClient.getTerminal() != null) {
                    if (sessionId.equals(terminalClient.getHttpSessionId())) {
                        terminalClients.add(terminalClient);
                    }
                }
            }
        }
        return terminalClients;
    }

    public static WebSocketSession findSession(Terminal terminal) {
        for (Map.Entry<WebSocketSession, TerminalClient> entry : terminalSession.entrySet()) {
            TerminalClient client = entry.getValue();
            if (client.getTerminal().equals(terminal)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void exit(HttpServletRequest request) throws IOException {
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        if (userAgent != null) {
            userAgent = userAgent.replaceAll("\\s+", "");
            userAgent = DigestUtils.md5Hex(userAgent);
        }

        if (notEmpty(terminalSession)) {
            for (Map.Entry<WebSocketSession, TerminalClient> entry : terminalSession.entrySet()) {
                TerminalClient terminalClient = entry.getValue();
                if (terminalClient.getHttpSessionId().equals(request.getSession().getId())
                        || terminalClient.getHttpSessionId().equals(userAgent)) {
                    terminalClient.disconnect();
                    terminalClient.getWebSocketSession().sendMessage(new TextMessage("Sorry! Session was invalidated, so jobx Terminal changed to closed. "));
                    terminalClient.getWebSocketSession().close();
                }
            }
        }
    }

}