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

package com.jobxhub.agent.bootstrap;

/**
 * Created by benjobs on 16/3/3.
 */

import com.jobxhub.agent.service.AgentService;
import com.jobxhub.agent.util.PropertiesLoader;
import com.jobxhub.common.Constants;
import com.jobxhub.common.ext.ExtensionLoader;
import com.jobxhub.common.logging.LoggerFactory;
import com.jobxhub.common.util.*;
import com.jobxhub.rpc.Server;
import com.jobxhub.rpc.ServerHandler;
import org.slf4j.Logger;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.AccessControlException;
import java.util.Random;
import java.util.concurrent.Executors;

import static com.jobxhub.common.util.CommonUtils.isEmpty;
import static com.jobxhub.common.util.CommonUtils.notEmpty;

public class JobXAgent implements Serializable {


    private static final long serialVersionUID = 20150614L;


    private static Logger logger = LoggerFactory.getLogger(JobXAgent.class);

    /**
     * rpc server
     */
    private Server server = ExtensionLoader.load(Server.class);

    /**
     * agent port
     */
    private Integer port;

    /**
     * agent password
     */
    private String password;

    /**
     * agent host
     */
    private String host;

    /**
     * bootstrap instance....
     */
    private static JobXAgent daemon;


    private volatile boolean stopAwait = false;

    /**
     * Server socket that is used to wait for the shutdown command.
     */
    private volatile ServerSocket awaitSocket = null;

    /**
     * The shutdown command string we are looking for.
     */
    private String shutdown = "stop";

    /**
     * A random number generator that is <strong>only</strong> used if
     * the shutdown command string is longer than 1024 characters.
     */
    private Random random = null;


    public static void main(String[] args) {
        if (daemon == null) {
            daemon = new JobXAgent();
        }

        try {
            if (isEmpty(args)) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Bootstrap: error,usage start|stop");
                }
            } else {
                String command = args[0];
                if ("start".equals(command)) {
                    daemon.init();
                    daemon.start();
                    /**
                     * await for shundown
                     */
                    daemon.await();
                    daemon.stopServer();
                    System.exit(0);
                } else if ("stop".equals(command)) {
                    daemon.shutdown();
                } else {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Bootstrap: command \"" + command + "\" does not exist.");
                    }
                }
            }
        } catch (Throwable t) {
            if (t instanceof InvocationTargetException && t.getCause() != null) {
                t = t.getCause();
            }
            handleThrowable(t);
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * init start........
     *
     * @throws Exception
     */
    private void init() {

        /**
         * port
         */
        String portStr = SystemPropertyUtils.get(Constants.PARAM_JOBX_PORT_KEY);
        if (isEmpty(portStr)) {
            portStr = PropertiesLoader.getProperty(Constants.PARAM_JOBX_PORT_KEY);
        }
        if (isEmpty(portStr)) {
            throw new ExceptionInInitializerError("[JobX] agent port must be not null");
        }
        this.port = CommonUtils.toInt(portStr, 0);
        if (NetUtils.isInvalidPort(this.port)) {
            throw new ExceptionInInitializerError("[JobX] agent port error,must be between 0 and 65535");
        }

        //host
        this.host = SystemPropertyUtils.get(Constants.PARAM_JOBX_HOST_KEY);
        if (isEmpty(this.host)) {
            this.host = PropertiesLoader.getProperty(Constants.PARAM_JOBX_HOST_KEY);
        }
        if (notEmpty(this.host) && NetUtils.isValidAddress(this.host)) {
            throw new ExceptionInInitializerError("[JobX] agent host is valid");
        }

        //password
        /**
         * 1)input
         * 2)passFile
         * 3)confFile
         * 先从启动脚本里读取password(-p{password}) 如果启动里未输入,则读取上次的密码.password,
         * 如果本地密码记录.password不存在,则从conf文件中读取密码,如果conf中未设置,报错退出...
         *
         */
        this.password = SystemPropertyUtils.get(Constants.PARAM_JOBX_PASSWORD_KEY);
        if (notEmpty(this.password)) {
            this.password = DigestUtils.md5Hex(this.password);
            Constants.JOBX_PASSWORD_FILE.delete();
            IOUtils.writeText(Constants.JOBX_PASSWORD_FILE, this.password, Constants.CHARSET_UTF8);
        } else {
            //.password file already exists
            if (Constants.JOBX_PASSWORD_FILE.exists()) {
                //read password from .password file
                this.password = IOUtils.readText(Constants.JOBX_PASSWORD_FILE, Constants.CHARSET_UTF8).trim();
            } else {
                //read pass from conf
                this.password = PropertiesLoader.getProperty(Constants.PARAM_JOBX_PASSWORD_KEY);
                if (notEmpty(this.password)) {
                    this.password = DigestUtils.md5Hex(this.password);
                    Constants.JOBX_PASSWORD_FILE.delete();
                    IOUtils.writeText(Constants.JOBX_PASSWORD_FILE, this.password, Constants.CHARSET_UTF8);
                } else {
                    throw new ExceptionInInitializerError("[JobX] agent password cat not be null");
                }
            }
        }

        if (!IOUtils.fileExists(Constants.JOBX_LOG_PATH)) {
            logger.warn("[JobX] logs folder is not found!make...");
            new File(Constants.JOBX_LOG_PATH).mkdir();
        }

        SystemPropertyUtils.setProperty(Constants.PARAM_JOBX_PASSWORD_KEY,this.password);

        //init native lib....
        String libPath = System.getProperty("java.library.path");
        if (!libPath.contains(Constants.JOBX_NATIVE_PATH)) {
            libPath += ";" + Constants.JOBX_NATIVE_PATH;
        }
        SystemPropertyUtils.setProperty(Constants.PARAM_JAVA_LIBRARY_PATH_KEY, libPath);
        String registryUrl = PropertiesLoader.getProperty(Constants.PARAM_JOBX_REGISTRY_KEY);
        SystemPropertyUtils.setProperty(Constants.PARAM_JOBX_REGISTRY_KEY,registryUrl);
    }

    private void start() {
        try {
            //new thread to start for netty server
            Executors.newSingleThreadExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    ServerHandler handler = ExtensionLoader.load(ServerHandler.class);
                    server.start(port, handler);
                }
            });

            /**
             * write pid to pidfile...
             */
            if (!CommonUtils.isWindows()) {
                Integer pid = getPid();
                IOUtils.writeText(Constants.JOBX_PID_FILE, pid, Constants.CHARSET_UTF8);
                if (logger.isInfoEnabled()) {
                    logger.info("[JobX]agent started @ port:{},pid:{}", port, pid);
                }
            }

            /**
             * 往zk里注册这一步一定要放在server(netty|mina)启动之后,不然如果先注册zk后启动server,则jobx-server端收到zk回调
             * 会发起rpc连接,而这时的jobx-agent里的server可能还未启动,导致连接失败,agent自动注册也会失败....
             */
            Thread.sleep(5000);
            AgentService.register(this.host,this.port);
            AgentService.bindShutdownHook(this.host,this.port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void await() throws Exception {
        // Negative values - don't wait on port - jobx is embedded or we just don't like ports
        if (port == -2) {
            return;
        }

        if (port == -1) {
            try {
                while (!stopAwait) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        // continue and check the flag
                    }
                }
            } finally {
            }
            return;
        }

        Integer shutdownPort = Integer.valueOf(PropertiesLoader.getProperty(Constants.PARAM_JOBX_SHUTDOWN_KEY));

        // Set up a server socket to wait on
        try {
            awaitSocket = new ServerSocket(shutdownPort);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("[JobX] agent .await: create[{}] ", shutdownPort, e);
            }
            return;
        }

        try {
            // Loop waiting for a connection and a valid command
            while (!stopAwait) {
                ServerSocket serverSocket = awaitSocket;
                if (serverSocket == null) {
                    break;
                }
                // Wait for the next connection
                Socket socket = null;
                StringBuilder command = new StringBuilder();
                try {
                    InputStream stream;
                    long acceptStartTime = System.currentTimeMillis();
                    try {
                        socket = serverSocket.accept();
                        socket.setSoTimeout(10 * 1000);  // Ten seconds
                        stream = socket.getInputStream();
                    } catch (SocketTimeoutException ste) {
                        // This should never happen but bug 56684 suggests that
                        // it does.
                        if (logger.isWarnEnabled()) {
                            logger.warn("[JobX] agentServer accept.timeout", Long.valueOf(System.currentTimeMillis() - acceptStartTime), ste);
                        }
                        continue;
                    } catch (AccessControlException ace) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("[JobX] agentServer .accept security exception: {}", ace.getMessage(), ace);
                        }
                        continue;
                    } catch (IOException e) {
                        if (stopAwait) {
                            break;
                        }
                        if (logger.isErrorEnabled()) {
                            logger.error("[JobX] agent .await: accept: ", e);
                        }
                        break;
                    }

                    // Read a set of characters from the socket
                    int expected = 1024; // Cut off to avoid DoS attack
                    while (expected < shutdown.length()) {
                        if (random == null) {
                            random = new Random();
                        }
                        expected += (random.nextInt() % 1024);
                    }
                    while (expected > 0) {
                        int ch = -1;
                        try {
                            ch = stream.read();
                        } catch (IOException e) {
                            if (logger.isWarnEnabled()) {
                                logger.warn("[JobX] agent .await: read: ", e);
                            }
                            ch = -1;
                        }
                        if (ch < 32)  // Control character or EOF terminates loop
                            break;
                        command.append((char) ch);
                        expected--;
                    }
                } finally {
                    try {
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException e) {
                    }
                }
                boolean match = command.toString().equals(shutdown);
                if (match) {
                    break;
                } else {
                    if (logger.isWarnEnabled()) {
                        logger.warn("[JobX] agent .await: Invalid command '" + command.toString() + "' received");
                    }
                }
            }
        } finally {
            ServerSocket serverSocket = awaitSocket;
            awaitSocket = null;
            // Close the server socket and return
            if (serverSocket != null) {
                try {
                    //unzookeeper before stop...
                    AgentService.unRegister(this.host,this.port);
                    serverSocket.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

    }

    /**
     * @throws Exception
     */

    private void shutdown() throws Exception {

        String address = "localhost";

        Integer shutdownPort = Integer.valueOf(PropertiesLoader.getProperty(Constants.PARAM_JOBX_SHUTDOWN_KEY));

        // Stop the existing server
        try {
            Socket socket = new Socket(address, shutdownPort);
            OutputStream stream = socket.getOutputStream();
            for (int i = 0; i < shutdown.length(); i++) {
                stream.write(shutdown.charAt(i));
            }
            stream.flush();
            socket.close();
        } catch (ConnectException ce) {
            if (logger.isErrorEnabled()) {
                logger.error("[JobX] Agent.stop error:{} ", ce);
            }
            System.exit(1);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("[JobX] Agent.stop error:{} ", e);
            }
            System.exit(1);
        }
    }

    private void stopServer() throws Throwable {
        this.server.destroy();
    }

    private static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath) t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError) t;
        }
    }

    private static Integer getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName();
        try {
            return Integer.parseInt(name.substring(0, name.indexOf('@')));
        } catch (Exception e) {
        }
        return -1;
    }

}

