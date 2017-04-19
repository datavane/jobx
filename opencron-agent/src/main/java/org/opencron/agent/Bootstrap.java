/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.opencron.agent;

/**
 * Created by benjobs on 16/3/3.
 */

import org.opencron.common.job.Opencron;
import org.opencron.common.utils.IOUtils;
import org.opencron.common.utils.LoggerFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.AccessControlException;
import java.util.Random;

import static org.opencron.common.utils.CommonUtils.isEmpty;
import static org.opencron.common.utils.CommonUtils.notEmpty;

public class Bootstrap implements Serializable {


    private static final long serialVersionUID = 20150614L;


    private static Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    /**
     * thrift server
     */
    private TServer server;

    /**
     * agent port
     */
    private int port;

    /**
     * agent password
     */
    private String password;

    /**
     * charset...
     */
    private final String CHARSET = "UTF-8";
    /**
     * bootstrap instance....
     */
    private static Bootstrap daemon;


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
            daemon = new Bootstrap();
        }

        try {
            if (isEmpty(args)) {
                logger.warn("Bootstrap: error,usage start|stop");
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
                } else if ("stop".equals(command)) {
                    daemon.shutdown();
                } else {
                    logger.warn("Bootstrap: command \"" + command + "\" does not exist.");
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
    public void init() throws Exception {
        port = Integer.valueOf(Integer.parseInt(Globals.OPENCRON_PORT));
        String inputPwd = Globals.OPENCRON_PASSWORD;
        if (notEmpty(inputPwd)) {
            this.password = DigestUtils.md5Hex(inputPwd).toLowerCase();
            Globals.OPENCRON_PASSWORD_FILE.deleteOnExit();
            IOUtils.writeText(Globals.OPENCRON_PASSWORD_FILE, this.password, CHARSET);
        } else {
            if (!Globals.OPENCRON_PASSWORD_FILE.exists()) {
                this.password = DigestUtils.md5Hex(this.password).toLowerCase();
                IOUtils.writeText(Globals.OPENCRON_PASSWORD_FILE, this.password, CHARSET);
            } else {
                password = IOUtils.readText(Globals.OPENCRON_PASSWORD_FILE, CHARSET).trim().toLowerCase();
            }
        }
    }

    public void start() throws Exception {
        try {
            TServerSocket serverTransport = new TServerSocket(port);
            AgentProcessor agentProcessor = new AgentProcessor(password);
            Opencron.Processor processor = new Opencron.Processor(agentProcessor);
            TBinaryProtocol.Factory protFactory = new TBinaryProtocol.Factory(true, true);
            TThreadPoolServer.Args arg = new TThreadPoolServer.Args(serverTransport);
            arg.protocolFactory(protFactory);
            arg.processor(processor);
            this.server = new TThreadPoolServer(arg);

            this.server.setServerEventHandler(new TServerEventHandler(){
                public void preServe() {}
                public void deleteContext(ServerContext serverContext, TProtocol tProtocol, TProtocol tProtocol1) {}
                public ServerContext createContext(TProtocol tProtocol, TProtocol tProtocol1) {return null;}
                public void processContext(ServerContext serverContext, TTransport inputTransport, TTransport outputTransport) {
                    TSocket socket = (TSocket) inputTransport;
                    //获取OPENCRON-server的ip
                    Globals.OPENCRON_SOCKET_ADDRESS = socket.getSocket().getRemoteSocketAddress().toString().substring(1);
                }
            });

            /**
             * write pid to pidfile...
             */
            IOUtils.writeText(Globals.OPENCRON_PID_FILE, getPid(), CHARSET);

            //new thread to start for thrift server
            new Thread(new Runnable() {
                @Override
                public void run() {
                    server.serve();
                }
            }).start();
            logger.info("[opencron]agent started @ port:{},pid:{}", port, getPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void await() throws Exception {
        // Negative values - don't wait on port - opencron is embedded or we just don't like ports
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

        Integer shutdownPort = Integer.valueOf(System.getProperty("opencron.shutdown"));
        // Set up a server socket to wait on
        try {
            awaitSocket = new ServerSocket(shutdownPort);
        } catch (IOException e) {
            logger.error("[opencron] agent .await: create[{}] ", shutdownPort, e);
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
                        logger.warn("[opencron] agentServer accept.timeout", Long.valueOf(System.currentTimeMillis() - acceptStartTime), ste);
                        continue;
                    } catch (AccessControlException ace) {
                        logger.warn("[opencron] agentServer .accept security exception: {}", ace.getMessage(), ace);
                        continue;
                    } catch (IOException e) {
                        if (stopAwait) {
                            break;
                        }
                        logger.error("[opencron] agent .await: accept: ", e);
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
                            logger.warn("[opencron] agent .await: read: ", e);
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
                    logger.warn("[opencron] agent .await: Invalid command '" + command.toString() + "' received");
                }
            }
        } finally {
            ServerSocket serverSocket = awaitSocket;
            awaitSocket = null;
            // Close the server socket and return
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

    }

    /**
     *
     * @throws Exception
     */

    public void shutdown() throws Exception {
        /**
         * connect to startup socket and send stop command。。。。。。
         */
        Socket socket = new Socket("localhost",Integer.valueOf(System.getProperty("opencron.shutdown")));
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        InputStream is = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        pw.write(shutdown);
        pw.flush();
        socket.shutdownOutput();
        String reply = null;
        while (!((reply = br.readLine()) == null)) {
            logger.info("[opencron]shutdown:{}" + reply);
        }
        br.close();
        is.close();
        pw.close();
        os.close();
        socket.close();
    }

    private void stopServer() {
        if (this.server != null && this.server.isServing()) {
            this.server.stop();
            /**
             * delete pid file...
             */
            Globals.OPENCRON_PID_FILE.deleteOnExit();
            System.exit(0);
        }
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

