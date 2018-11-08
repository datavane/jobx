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

package com.jobxhub.agent.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.base.Joiner;

import java.io.*;
import java.nio.charset.Charset;

import com.jobxhub.common.Constants;
import com.jobxhub.common.util.CommonUtils;

public class ProcessLogger extends Thread {

    private final BufferedReader inputReader;
    private final Logger logger;
    private final Level loggingLevel;
    private final CircularBuffer<String> buffer;

    public ProcessLogger(final Reader inputReader, final Logger logger, final Level level, final int bufferLines) {
        this.inputReader = new BufferedReader(inputReader);
        this.logger = logger;
        this.loggingLevel = level;
        this.buffer = new CircularBuffer<String>(bufferLines);
    }

    public static ProcessLogger getLoger(InputStream inputStream, Logger logger, Level level) {
        return new ProcessLogger(
                new InputStreamReader(inputStream, Charset.forName(CommonUtils.isWindows() ? Constants.CHARSET_GBK : Constants.CHARSET_UTF8)),
                logger,
                level,
                30);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                final String line = this.inputReader.readLine();
                if (line == null) {
                    return;
                }
                this.buffer.append(line);
                log(line);
            }
        } catch (final IOException e) {
            error("[JobX]Error reading from logging stream:", e);
        }
    }

    private void log(final String message) {
        if (this.logger != null) {
            this.logger.log(null, ProcessLogger.class.getName(), this.loggingLevel.levelInt, message, null, null);
        }
    }

    private void error(final String message, final Exception e) {
        if (this.logger != null) {
            this.logger.error(message, e);
        }
    }

    private void info(final String message, final Exception e) {
        if (this.logger != null) {
            this.logger.info(message, e);
        }
    }

    public void awaitCompletion(final long waitMs) {
        try {
            join(waitMs);
        } catch (final InterruptedException e) {
            info("[JobX]I/O thread interrupted.", e);
        }
    }

    public String getRecentLog() {
        return Joiner.on(System.getProperty("line.separator")).join(this.buffer);
    }

}
