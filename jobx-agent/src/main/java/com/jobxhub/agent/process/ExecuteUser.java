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

package com.jobxhub.agent.process;

import com.jobxhub.common.Constants;
import com.jobxhub.common.util.IOUtils;
import com.jobxhub.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExecuteUser {

    private final static Logger logger = LoggerFactory.getLogger(ExecuteUser.class);

    public ExecuteUser() {
        if (!IOUtils.fileExists(Constants.JOBX_EXECUTE_AS_USER_LIB)) {
            throw new RuntimeException("[JobX]not found ExecuteUser binary. Invalid Path: " + Constants.JOBX_EXECUTE_AS_USER_LIB);
        }
    }

    /**
     * API to execute a command on behalf of another user.
     *
     * @param user The proxy user
     * @param command the list containing the program and its arguments
     * @return The return value of the shell command
     */
    public int execute(final String user, final List<String> command) throws IOException {
        logger.info("[Jobx]execute Command {} ",StringUtils.joinString(command));
        final Process process = new ProcessBuilder()
                .command(buildCommand(user, command))
                .inheritIO()
                .start();
        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (final InterruptedException e) {
            logger.error(e.getMessage(), e);
            exitCode = 1;
        }
        return exitCode;
    }

    public static List<String> buildCommand(final String user, final List<String> command) {
        final List<String> commandList = new ArrayList<String>();
        commandList.add(Constants.JOBX_EXECUTE_AS_USER_LIB);
        commandList.add(user);
        commandList.addAll(command);
        return commandList;
    }
}
