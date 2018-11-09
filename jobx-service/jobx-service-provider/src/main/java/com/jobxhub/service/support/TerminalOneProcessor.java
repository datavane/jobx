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
import com.jobxhub.common.ext.MethodMark;
import com.jobxhub.service.service.TerminalService;
import com.jobxhub.service.vo.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URLDecoder;
import java.util.List;

@Component
public class TerminalOneProcessor {

    @Autowired
    private TerminalService termService;

    @MethodMark
    public Status sendAll(String token, String cmd) throws Exception {
        cmd = URLDecoder.decode(cmd, Constants.CHARSET_UTF8);
        TerminalClient terminalClient = TerminalSession.get(token);
        if (terminalClient != null) {
            List<TerminalClient> terminalClients = TerminalSession.findClient(terminalClient.getHttpSessionId());
            for (TerminalClient client : terminalClients) {
                client.write(cmd);
            }
        }
        return Status.TRUE;
    }

    @MethodMark
    public Status theme(String token, String theme) throws Exception {
        TerminalClient terminalClient = TerminalSession.get(token);
        if (terminalClient != null) {
            termService.theme(terminalClient.getTerminal(), theme);
        }
        return Status.TRUE;
    }

    @MethodMark
    public Status resize(String token, Integer cols, Integer rows, Integer width, Integer height) throws Exception {
        TerminalClient terminalClient = TerminalSession.get(token);
        if (terminalClient != null) {
            terminalClient.resize(cols, rows, width, height);
        }
        return Status.TRUE;
    }

    @MethodMark
    public Status upload(String token, File file, String name, long size) {
        TerminalClient terminalClient = TerminalSession.get(token);
        boolean success = true;
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            if (terminalClient != null) {
                terminalClient.upload(file.getAbsolutePath(), name, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        return Status.create(success);
    }

}
