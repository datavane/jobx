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

    private final String command;
    private final int timeout;
    private final CountDownLatch startupLatch;
    private final CountDownLatch completeLatch;
    private Integer processId;
    private ExitCode kill;
    private File logFile;
    private File execShell;
    private Process process;
    private String execUser;

    public JobXProcess(String command, int timeout, String pid, String execUser) {
        this.timeout = timeout;
        this.logFile = getLogFile(pid);
        this.processId = -1;
        this.processLogger = this.getLogger(pid);
        this.startupLatch = new CountDownLatch(1);
        this.completeLatch = new CountDownLatch(1);
        this.execUser = execUser;
        this.execShell = getExecShell(pid);
        this.command = ExecuteUser.buildCommand(execUser, execShell, command);
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
            this.watchTimeOut();
            this.process = Runtime.getRuntime().exec(this.command);
            this.processId = getProcessId();
            if (this.processId == 0) {
                this.logger.info("[JobX]Spawned thread with unknown process id");
            } else {
                this.logger.info("[JobX]Spawned thread with process id " + this.processId);
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
        if (CommonUtils.notEmpty(this.logFile)) {
            this.logFile.delete();
        }
    }

    public void deleteExecShell() {
        if (CommonUtils.notEmpty(this.execShell)) {
            this.execShell.delete();
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
                if (CommonUtils.isUnix()) {
                    boolean killed = this.softKill(1000*5,TimeUnit.SECONDS);
                    if (!killed) {
                        this.hardKill();
                    }
                }else {
                    this.hardKill();
                }
            }catch (Exception e) {
                e.printStackTrace();
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
                String KillCMD = String.format("/bin/bash +x %s %d %d",Constants.JOBX_KILL_FILE.getAbsolutePath(),0,this.processId);
                if (isExecAsUser()) {
                    KillCMD = String.format(
                            "%s %s %s",
                            Constants.JOBX_EXECUTE_AS_USER_LIB,
                            this.execUser,
                            KillCMD
                    );
                }
                Process process = Runtime.getRuntime().exec(KillCMD);
                process.waitFor();
                process.destroy();
                this.processLogger.error("[JobX]hardKill successful,pid:" + this.processId);
                return this.completeLatch.await(time, unit);
            } catch (IOException e) {
                this.processLogger.error("[JobX]softKill failed.pid:" + this.processId);
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
                String killCMD = String.format("/bin/bash +x %s %d %d",Constants.JOBX_KILL_FILE.getAbsolutePath(),1,this.processId);
                if (CommonUtils.isUnix()) {
                    if (isExecAsUser()) {
                        killCMD = String.format("%s %s %s",
                                Constants.JOBX_EXECUTE_AS_USER_LIB,
                                this.execUser,
                                killCMD);
                    }
                }else if(CommonUtils.isWindows()) {
                    killCMD = String.format("cmd.exe /c taskkill /PID %s /F /T ",this.processId) ;
                }
                Process process = Runtime.getRuntime().exec(killCMD);
                process.waitFor();
                process.destroy();
                this.processLogger.error("[JobX]hardKill successful.");
            }catch (Exception e) {
                this.processLogger.error("[JobX]hardKill failed.", e);
            }
            this.processId = null;
        }
    }

    private Integer getProcessId() {
        try {
            if (this.process == null) return null;
            if (CommonUtils.isUnix()) {
                Field field = ReflectUtils.getField(this.process.getClass(), "pid");
                return field.getInt(this.process);
            }else if(CommonUtils.isWindows()) {
                Field field = ReflectUtils.getField(this.process.getClass(), "handle");
                WinNT.HANDLE handle = new WinNT.HANDLE();
                long handl = field.getLong(this.process);
                handle.setPointer(Pointer.createConstant(handl));
                return Kernel32.INSTANCE.GetProcessId(handle);
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

    private File getExecShell(String pid) {
        return new File(String.format("%s/.%s.%s",Constants.JOBX_TMP_PATH,pid,CommonUtils.isWindows()?"bat":"sh"));
    }

    private File getLogFile(String pid) {
        return new File(Constants.JOBX_LOG_PATH + "/." + pid + ".log");
    }

}
