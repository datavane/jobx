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

package com.jobxhub.server.support;

import com.jcraft.jsch.*;
import com.jobxhub.common.Constants;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.DigestUtils;
import com.jobxhub.common.util.IOUtils;
import com.jobxhub.common.util.StringUtils;
import com.jobxhub.server.dto.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.List;

import static com.jobxhub.common.util.CommonUtils.notEmpty;

public class TerminalClient {

    private final Logger logger = LoggerFactory.getLogger(TerminalClient.class);

    private String clientId;//每次生成的唯一值token
    private String httpSessionId;//打开该终端的SessionId
    private WebSocketSession webSocketSession;
    private JSch jSch;
    private ChannelShell channelShell;
    private Session session;
    private Terminal terminal;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedWriter writer;

    //获取路径相关变量
    private String pwd;
    private boolean sendTempCmd = false;
    private String sendTempCmdId;

    //控制命令(exit)终端退出相关变量
    private boolean sendEnter;
    private boolean receiveEnd = false;
    private StringBuffer sendBuffer = new StringBuffer();

    //连接时长相关变量
    public static final int SERVER_ALIVE_INTERVAL = 60 * 1000;
    public static final int SESSION_TIMEOUT = 60000;
    public static final int CHANNEL_TIMEOUT = 60000;

    private boolean closed = false;

    public TerminalClient(WebSocketSession webSocketSession, Terminal terminal, String clientId) {
        this.webSocketSession = webSocketSession;
        this.terminal = terminal;
        this.clientId = clientId;

        //解决分布式环境下,不同的server为用户打开的终端实例会话不一致问题,导致根据会话Id查找不到另一个server打开的终端实例的问题
        List<String> userAgents = webSocketSession.getHandshakeHeaders().get(HttpHeaders.USER_AGENT);
        if (CommonUtils.notEmpty(userAgents)) {
            String userAgent = StringUtils.joinString(userAgents);
            userAgent = userAgent.replaceAll("\\s+", "");
            this.httpSessionId = DigestUtils.md5Hex(userAgent);
        } else {
            this.httpSessionId = (String) webSocketSession.getAttributes().get(Constants.PARAM_HTTP_SESSION_ID_KEY);
        }
        this.sendTempCmdId = this.clientId + this.httpSessionId;
        this.jSch = new JSch();
    }

    public void openTerminal(final int cols, int rows, int width, int height) throws Exception {
        this.session = jSch.getSession(terminal.getUserName(), terminal.getHost(), terminal.getPort());
        Constants.SshType sshType = Constants.SshType.getByType(terminal.getSshType());
        switch (sshType) {
            case SSHKEY:
                if (notEmpty(terminal.getPrivateKey())) {
                    File keyFile = new File(terminal.getPrivateKeyPath());
                    if (!keyFile.exists()) {
                        //将数据库中的私钥写到用户的机器上
                        IOUtils.writeFile(keyFile, new ByteArrayInputStream(terminal.getPrivateKey()));
                    }
                    if (notEmpty(terminal.getPhrase())) {
                        //设置带口令的密钥
                        jSch.addIdentity(terminal.getPrivateKeyPath(), terminal.getPhrase());
                    } else {
                        //设置不带口令的密钥
                        jSch.addIdentity(terminal.getPrivateKeyPath());
                    }
                    UserInfo userInfo = new SshUserInfo();
                    session.setUserInfo(userInfo);
                }
                session.setConfig("StrictHostKeyChecking", "no");
                break;
            case ACCOUNT:
                session.setConfig("StrictHostKeyChecking", "no");
                session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
                session.setPassword(terminal.getPassword());
                break;
        }
        this.session.setServerAliveInterval(SERVER_ALIVE_INTERVAL);
        this.session.connect(SESSION_TIMEOUT);
        this.channelShell = (ChannelShell) session.openChannel("shell");
        this.channelShell.setPtyType("xterm", cols, rows, width, height);
        this.inputStream = this.channelShell.getInputStream();
        this.outputStream = this.channelShell.getOutputStream();
        this.writer = new BufferedWriter(new OutputStreamWriter(this.outputStream, "UTF-8"));
        this.channelShell.connect();

        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024 * 4];
                StringBuilder builder = new StringBuilder();
                try {
                    while (webSocketSession != null && webSocketSession.isOpen()) {
                        builder.setLength(0);
                        int size = inputStream.read(buffer);
                        if (size == -1) {
                            return;
                        }
                        for (int i = 0; i < size; i++) {
                            char chr = (char) (buffer[i] & 0xff);
                            builder.append(chr);
                        }
                        //取到linux远程机器输出的信息发送给前端
                        String message = builder.toString();
                        message = new String(message.getBytes(DigestUtils.getEncoding(message)), "UTF-8");
                        //获取pwd的结果输出,不能发送给前台
                        if (sendTempCmd) {
                            if (message.contains(sendTempCmdId)) {
                                if (pwd != null || message.contains("echo")) {
                                    continue;
                                }
                                pwd = message.replace(sendTempCmdId, "").replaceAll("\r\n.*", "") + "/";
                                if (logger.isInfoEnabled()) {
                                    logger.info("[JobX] Sftp upload file target path:{}", pwd);
                                }
                            }
                        } else {
                            webSocketSession.sendMessage(new TextMessage(message));
                            if (sendEnter) {
                                String trimMessage = message.replaceAll("\r\n", "");
                                if (CommonUtils.isEmpty(trimMessage)) {
                                    receiveEnd = false;
                                } else {
                                    if (sendBuffer.toString().equals("exit") && trimMessage.equals("logout")) {
                                        closed = true;
                                    }
                                    receiveEnd = true;
                                }
                                if (receiveEnd) {
                                    sendBuffer.setLength(0);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();

    }

    /**
     * 向ssh终端输入内容
     *
     * @param message
     * @throws IOException
     */
    public void write(String message) throws IOException {
        if (message.equals("\r")) {
            if (closed) {
                disconnect();
            } else {
                sendEnter = true;
            }
        } else {
            sendEnter = false;
            sendBuffer.append(message);
        }

        if (writer != null) {
            writer.write(message);
            writer.flush();
        }
    }

    public void upload(String src, String dst, long fileSize) throws Exception {
        FileInputStream file = new FileInputStream(src);
        ChannelSftp channelSftp = (ChannelSftp) this.session.openChannel("sftp");
        channelSftp.connect(CHANNEL_TIMEOUT);
        //当前路径下,获取用户终端的当前位置
        if (dst.startsWith("./")) {
            //不出绝招不行啊....很神奇
            sendTempCmd = true;
            write(String.format("echo %s$(pwd)\r", this.sendTempCmdId));
            //等待获取返回的路径...
            Thread.sleep(100);
            sendTempCmd = false;
            if (pwd == null) {
                throw new RuntimeException("[JobX] Sftp upload file target path error!");
            }
            dst = dst.replaceFirst("\\./", pwd);
            pwd = null;
        } else if (dst.startsWith("~")) {
            dst = dst.replaceAll("~/|~", "");
        }
        channelSftp.put(file, dst, new SftpMonitor(fileSize));
        //exit
        if (channelSftp != null) {
            channelSftp.exit();
        }
        //disconnect
        if (channelSftp != null) {
            channelSftp.disconnect();
        }
    }

    public void disconnect() throws IOException {
        if (writer != null) {
            writer.close();
            writer = null;
        }
        if (session != null) {
            session.disconnect();
            session = null;
        }
        if (jSch != null) {
            jSch = null;
        }
        closed = true;
    }

    public void resize(Integer cols, Integer rows, Integer width, Integer height) throws IOException {
        channelShell.setPtySize(cols, rows, width, height);
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public boolean isClosed() {
        return closed;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public String getHttpSessionId() {
        return httpSessionId;
    }

    public String getClientId() {
        return clientId;
    }

}