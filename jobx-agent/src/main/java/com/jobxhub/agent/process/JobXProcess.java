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

import com.jobxhub.agent.util.ProcessLogger;
import com.jobxhub.common.Constants;
import com.jobxhub.common.Constants.ExitCode ;
import com.jobxhub.common.logging.LoggerFactory;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.IOUtils;
import com.jobxhub.common.util.ReflectUtils;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import org.apache.log4j.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author benjobs
 */

public class JobXProcess {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(JobXProcess.class);

    private Logger processLogger;

    public static String KILL_COMMAND = "kill";

    private final String command;
    private final int timeout;
    private final CountDownLatch startupLatch;
    private final CountDownLatch completeLatch;
    private ExitCode  kill;
    private File logFile;
    private Integer processId;
    private Process process;
    private String execUser;
    private final String runAsUserBinary = Constants.JOBX_EXECUTE_AS_USER_LIB;

    public JobXProcess(String command, Integer timeout, String pid, String execUser) {
        this.timeout = timeout;
        this.logFile = new File(Constants.JOBX_LOG_PATH + "/." + pid + ".log");
        this.processId = -1;
        this.processLogger = this.getLogger(pid);
        this.startupLatch = new CountDownLatch(1);
        this.completeLatch = new CountDownLatch(1);
        this.execUser = execUser;
        this.command = ExecuteUser.buildCommand(execUser,command);
    }

    /**
     * Execute this process, blocking until it has completed.
     */
    public int start() {
        if (this.isStarted() || this.isComplete()) {
            throw new IllegalStateException("[JobX]The process can only be used once.");
        }

        int exitCode = -1;
        try {

           /* ProcessBuilder builder = new ProcessBuilder(this.command);
            builder.directory(new File(this.workingDir));
            builder.redirectErrorStream(true);
*/
            this.watchTimeOut();

            this.process = Runtime.getRuntime().exec(command);
            this.processId = getProcessId();
            if (processId == null) {
                this.logger.debug("[JobX]Spawned thread with unknown process id");
            } else {
                this.logger.debug("[JobX]Spawned thread with process id " + processId);
            }
            this.startupLatch.countDown();
            ProcessLogger outputLogger = ProcessLogger.getLoger(this.process.getInputStream(), this.processLogger, Level.INFO);
            ProcessLogger errorLogger = ProcessLogger.getLoger(this.process.getErrorStream(), this.processLogger, Level.ERROR);
            outputLogger.start();
            errorLogger.start();
            try {
                exitCode = this.process.waitFor();
            } catch (InterruptedException e) {
                this.logger.info("[JobX]Process interrupted. Exit code is " + exitCode, e);
            }

            this.completeLatch.countDown();

            // try to wait for everything to get logged out before exiting
            outputLogger.awaitCompletion(1000);
            errorLogger.awaitCompletion(1000);

            if (exitCode != 0) {
                String output = new StringBuilder()
                        .append("Stdout:\n")
                        .append(outputLogger.getRecentLog())
                        .append("\n\n")
                        .append("Stderr:\n")
                        .append(errorLogger.getRecentLog())
                        .append("\n")
                        .toString();
                throw new ProcessException(exitCode, output);
            }
        } finally {
            IOUtils.closeQuietly(this.process.getInputStream());
            IOUtils.closeQuietly(this.process.getOutputStream());
            IOUtils.closeQuietly(this.process.getErrorStream());

            //最后以特殊不了见的字符作为log和exitCode+结束时间的分隔符.
            this.processLogger.info(IOUtils.FIELD_TERMINATED_BY + exitCode + IOUtils.TAB + new Date().getTime());
            this.process.destroy();
            if (this.kill!=null) {
                exitCode = this.kill.getValue();
            }
            return exitCode;
        }
    }

    private void watchTimeOut() {
        if (this.timeout > 0) {
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //kill job...
                    kill(Constants.ExitCode.TIME_OUT);
                    timer.cancel();
                }
            }, timeout * 60 * 1000);
        }
    }

    /**
     * read message from log
     *
     * @return String
     */
    public String getLogMessage() {
        String log = IOUtils.readText(this.logFile, CommonUtils.isWindows()?Constants.CHARSET_GBK:Constants.CHARSET_UTF8);
        if (CommonUtils.notEmpty(log)) {
            return log.split(IOUtils.FIELD_TERMINATED_BY)[0];
        }
        return null;
    }

    public void deleteLog() {
        if (this.logFile.exists()) {
            this.logFile.delete();
        }
    }

    /**
     * Await the completion of this process
     *
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    public void awaitCompletion() throws InterruptedException {
        this.completeLatch.await();
    }

    /**
     * Await the start of this process
     * <p>
     * When this method returns, the job process has been created and a this.processId has been set.
     *
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    public void awaitStartup() throws InterruptedException {
        this.startupLatch.await();
    }

    public void kill(ExitCode  kill) {
        if (isStarted()) {
            this.kill = kill;
            try {
                if (CommonUtils.isWindows()) {
                    hardKill();
                }else {
                    boolean flag = softKill(1000*5,TimeUnit.SECONDS);
                    if (!flag) {
                        hardKill();
                    }
                }
            }catch (Exception e) {
                logger.info("[JobX]Kill attempt failed：{}",e.getMessage());
            }
        }
    }

    /**
     * Attempt to kill the process, waiting up to the given time for it to die
     *
     * @param time The amount of time to wait
     * @param unit The time unit
     * @return true iff this soft kill kills the process in the given wait time.
     */
    private boolean softKill(long time, TimeUnit unit) throws InterruptedException {
        if (this.processId != 0 && isStarted()) {
            try {
                if (isExecAsUser()) {
                    String cmd = String.format(
                            "%s %s %s %d",
                            runAsUserBinary,
                            this.execUser,
                            KILL_COMMAND,
                            this.processId
                    );
                    Runtime.getRuntime().exec(cmd);
                } else {
                    String cmd = String.format("%s %d", KILL_COMMAND, this.processId);
                    Runtime.getRuntime().exec(cmd);
                }
                return this.completeLatch.await(time, unit);
            } catch (IOException e) {
                this.processLogger.error("[JobX]Kill attempt failed.", e);
            }
            return false;
        }
        return false;
    }

    /**
     * Force kill this process
     */
    private void hardKill() {
        if ( isRunning() && this.processId != null ) {
            try {
                String cmd = "";
                if (CommonUtils.isUnix()) {
                    if (isExecAsUser()) {
                        cmd = String.format("%s %s %s -9 %d",
                                this.runAsUserBinary,
                                this.execUser, KILL_COMMAND,
                                this.processId);
                    } else {
                        cmd = String.format("%s -9 %d", KILL_COMMAND, this.processId);
                    }
                }else if(CommonUtils.isWindows()) {
                    cmd = String.format("cmd.exe /c taskkill /PID %s /F /T ",this.processId) ;
                }
                Process process = Runtime.getRuntime().exec(cmd);
                process.waitFor();
                process.destroy();
                this.processLogger.error("[JobX]Kill attempt successful.");
            }catch (Exception e) {
                this.processLogger.error("[JobX]Kill attempt failed.", e);
            }
            this.processId = null;
        }
    }

    /**
     * Attempt to get the process id for this process
     *
     * @return The id of the process
     */
    private Integer getProcessId() {
        try {
            if (this.process == null) return null;
            if (CommonUtils.isUnix()) {
                Field field = ReflectUtils.getField(this.process.getClass(), "pid");
                return field.getInt(this.process);
            }else if(CommonUtils.isWindows()) {
                Field field = ReflectUtils.getField(this.process.getClass(), "handle");
                field.setAccessible(true);
                Kernel32 kernel = Kernel32.INSTANCE;
                WinNT.HANDLE handle = new WinNT.HANDLE();
                long handl = field.getLong(this.process);
                handle.setPointer(Pointer.createConstant(handl));
                return kernel.GetProcessId(handle);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return true iff the process has been started
     */
    public boolean isStarted() {
        return this.startupLatch.getCount() == 0L;
    }

    /**
     * @return true iff the process has completed
     */
    public boolean isComplete() {
        return this.completeLatch.getCount() == 0L;
    }

    /**
     * @return true iff the process is currently running
     */
    public boolean isRunning() {
        return isStarted() && !isComplete();
    }

    public void checkStarted() {
        if (!isStarted()) {
            throw new IllegalStateException("[JobX]Process has not yet started.");
        }
    }

    /**
     * runUser only support linux...
     * @return
     */
    public boolean isExecAsUser() {
        return CommonUtils.isLinux() && CommonUtils.notEmpty(execUser);
    }

    private Logger getLogger(String name) {
        FileAppender appender = new RollingFileAppender();
        appender.setEncoding(CommonUtils.isWindows()?Constants.CHARSET_GBK:Constants.CHARSET_UTF8);
        appender.setFile(this.logFile.getAbsolutePath());
        appender.setAppend(false);
        PatternLayout layout = new PatternLayout();
        appender.setLayout(layout);
        appender.activateOptions();
        Logger logger = Logger.getLogger(name);
        logger.setLevel(Level.INFO);
        logger.setAdditivity(false);
        logger.removeAllAppenders();
        logger.addAppender(appender);
        return logger;
    }

}
