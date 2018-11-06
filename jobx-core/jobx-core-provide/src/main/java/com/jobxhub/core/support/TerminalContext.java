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

package com.jobxhub.core.support;

import com.jobxhub.common.Constants;
import com.jobxhub.core.model.Terminal;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import com.jobxhub.common.util.collection.HashMap;


@Component
public class TerminalContext implements Serializable {

    //key-->token value--->Terminal
    public static Map<String, Terminal> terminalContext = new HashMap<String, Terminal>();

    private String token;

    public Terminal get(String key) {
        if (Constants.JOBX_CLUSTER) {
            return JobXTools.getCachedManager().get(key(key), Terminal.class);
        } else {
            return terminalContext.get(key(key));
        }
    }

    public void put(String key, Terminal terminal) {
        if (Constants.JOBX_CLUSTER) {
            JobXTools.getCachedManager().set(Constants.PARAM_TERMINAL_TOKEN_KEY, key);
            JobXTools.getCachedManager().set(key(key), terminal);
            /**
             * 为复制会话
             */
            String reKey = terminal.getId() + "_" + key;
            JobXTools.getCachedManager().set(key(reKey), terminal);
        } else {
            this.token = key;
            //该终端实例只能被的打开一次,之后就失效
            terminalContext.put(key(key), terminal);
            /**
             * 为复制会话
             */
            String reKey = terminal.getId() + "_" + key;
            terminalContext.put(key(reKey), terminal);
        }
    }

    public Terminal remove(String key) {
        if (Constants.JOBX_CLUSTER) {
            Terminal terminal = get(key);
            JobXTools.getCachedManager().delete(key(key));
            return terminal;
        } else {
            return terminalContext.remove(key(key));
        }
    }

    private String key(String key) {
        return Constants.PARAM_TERMINAL_PREFIX_KEY + key;
    }

    public String getToken() {
        if (Constants.JOBX_CLUSTER) {
            return JobXTools.getCachedManager().remove(Constants.PARAM_TERMINAL_TOKEN_KEY, String.class);
        } else {
            return this.token;
        }
    }

}