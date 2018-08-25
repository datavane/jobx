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
import com.jobxhub.common.util.AssertUtils;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.jobxhub.common.util.CommandUtils.*;

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
     * @param user    The proxy user
     * @param command the list containing the program and its arguments
     * @return The return value of the shell command
     */
    public static int execute(final String user, final File file, final String command) throws IOException {
        logger.info("[Jobx]execute Command {} ", command);
        final Process process = new ProcessBuilder()
                .command(buildCommand(user, file, command))
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

    public static String buildCommand(final String proxyUser, final File execFile, final String command) {
        AssertUtils.notNull(command);
        if (CommonUtils.isUnix()) {
            //写入命令到文件
            write(execFile, command);
            String execCmd = String.format("/bin/bash +x %s", execFile.getAbsolutePath());
            if (CommonUtils.notEmpty(proxyUser)) {
                //授权文件...
                try {
                    chownFile(execFile, proxyUser);
                } catch (Exception e) {
                    throw new RuntimeException("[JobX] chown command file error,{}", e.getCause());
                }
                return Constants.JOBX_EXECUTE_AS_USER_LIB
                        .concat(IOUtils.BLANK_CHAR)
                        .concat(proxyUser)
                        .concat(IOUtils.BLANK_CHAR)
                        .concat(execCmd);

            }
            return execCmd;
        }
        return command;
    }
}
