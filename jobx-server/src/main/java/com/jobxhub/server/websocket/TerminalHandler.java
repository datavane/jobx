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

package com.jobxhub.server.websocket;


import java.io.IOException;


import static com.jobxhub.common.util.CommonUtils.toInt;

import com.jobxhub.server.service.TerminalService;
import com.jobxhub.server.support.*;
import com.jobxhub.server.dto.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


public class TerminalHandler extends TextWebSocketHandler {

    private TerminalClient terminalClient;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private TerminalContext terminalContext;

    @Autowired
    private TerminalClusterProcessor terminalClusterProcessor;

    private String terminalToken;

    @Override
    public synchronized void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        terminalToken = terminalContext.getToken();
        if (terminalToken != null) {
            final Terminal terminal = terminalContext.remove(terminalToken);
            if (terminal != null) {
                try {
                    session.sendMessage(new TextMessage("Welcome to jobx Terminal! Connect Starting."));
                    getClient(session, terminal);
                    int cols = toInt(session.getAttributes().get("cols").toString());
                    int rows = toInt(session.getAttributes().get("rows").toString());
                    int width = toInt(session.getAttributes().get("width").toString());
                    int height = toInt(session.getAttributes().get("height").toString());
                    terminalClient.openTerminal(cols, rows, width, height);
                    terminalService.login(terminal.getId());
                } catch (Exception e) {
                    if (e.getLocalizedMessage().replaceAll("\\s+", "").contentEquals("Operationtimedout")) {
                        session.sendMessage(new TextMessage("Sorry! Connect timed out, please try again. "));
                    } else {
                        session.sendMessage(new TextMessage("Sorry! Operation error, please try again. "));
                    }
                    terminalClient.disconnect();
                    session.close();
                }
            } else {
                this.terminalClient.disconnect();
                session.sendMessage(new TextMessage("Sorry! Connect failed, please try again. "));
                session.close();
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        try {
            getClient(session, null);
            if (this.terminalClient != null) {
                if (!terminalClient.isClosed()) {
                    terminalClient.write(message.getPayload());
                } else {
                    session.close();
                }
            }
        } catch (Exception e) {
            session.sendMessage(new TextMessage("Sorry! jobx Terminal was closed, please try again. "));
            terminalClient.disconnect();
            session.close();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        this.closeTerminal(session);
        session.close();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        this.closeTerminal(session);
    }

    private TerminalClient getClient(WebSocketSession session, Terminal terminal) {
        this.terminalClient = TerminalSession.get(session);
        if (this.terminalClient == null && terminal != null) {
            this.terminalClient = new TerminalClient(session, terminal, terminalToken);
            TerminalSession.put(session, this.terminalClient);
        }
        return this.terminalClient;
    }

    private void closeTerminal(WebSocketSession session) throws IOException {
        terminalClient = TerminalSession.remove(session);
        if (terminalClient != null) {
            terminalClient.disconnect();
        }
        //解除实例
        terminalClusterProcessor.unregistry(terminalToken);
    }

}

