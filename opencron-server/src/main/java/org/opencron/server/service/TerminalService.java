/**
 * Copyright 2016 benjobs
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

package org.opencron.server.service;

import com.jcraft.jsch.*;
import org.opencron.common.utils.CommonUtils;
import org.opencron.common.utils.DigestUtils;
import org.opencron.server.dao.QueryDao;
import org.opencron.server.domain.Terminal;
import org.opencron.server.domain.User;
import org.opencron.server.job.OpencronTools;
import org.opencron.server.tag.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.crypto.BadPaddingException;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.opencron.common.utils.CommonUtils.notEmpty;
import static org.opencron.server.job.OpencronTools.HTTP_SESSION_ID;
import static org.opencron.server.job.OpencronTools.SSH_SESSION_ID;


/**
 * @author <a href="mailto:benjobs@qq.com">benjobs@qq.com</a>
 * @name:CommonUtil
 * @version: 1.0.0
 * @company: org.opencron
 * @description: webconsole核心类
 * @date: 2016-05-25 10:03<br/><br/>
 * <p>
 * <b style="color:RED"></b><br/><br/>
 * 你快乐吗?<br/>
 * 风轻轻的问我<br/>
 * 曾经快乐过<br/>
 * 那时的湖面<br/>
 * 她踏着轻舟泛过<br/><br/>
 * <p>
 * 你忧伤吗?<br/>
 * 雨悄悄的问我<br/>
 * 一直忧伤着<br/>
 * 此时的四季<br/>
 * 全是她的柳絮飘落<br/><br/>
 * <p>
 * 你心痛吗?<br/>
 * 泪偷偷的问我<br/>
 * 心痛如刀割<br/>
 * 收到记忆的包裹<br/>
 * 都是她冰清玉洁还不曾雕琢<br/><br/>
 * <p>
 * <hr style="color:RED"/>
 */

@Service
public class TerminalService {

    private static Logger logger = LoggerFactory.getLogger(TerminalService.class);

    @Autowired
    private QueryDao queryDao;

    public boolean exists(Long userId, String host) throws Exception {
        Terminal terminal = queryDao.sqlUniqueQuery(Terminal.class, "SELECT * FROM T_TERMINAL WHERE userId=? AND host=?", userId, host);
        return terminal != null;
    }

    public boolean saveOrUpdate(Terminal term) throws Exception {
        Terminal dbTerm = queryDao.sqlUniqueQuery(Terminal.class, "SELECT * FROM T_TERMINAL WHERE ID=?", term.getId());
        if (dbTerm != null) {
            term.setId(dbTerm.getId());
        }
        try {
            queryDao.save(term);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Terminal.AuthStatus auth(Terminal terminal) {
        JSch jSch = new JSch();
        Session session = null;
        try {
            session = jSch.getSession(terminal.getUserName(), terminal.getHost(), terminal.getPort());
            session.setPassword(terminal.getPassword());

            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
            session.connect(TerminalClient.SESSION_TIMEOUT);
            return Terminal.AuthStatus.SUCCESS;
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().contains("userauth fail")) {
                return Terminal.AuthStatus.PUBLIC_KEY_FAIL;
            } else if (e.getMessage().toLowerCase().contains("auth fail") || e.getMessage().toLowerCase().contains("auth cancel")) {
                return Terminal.AuthStatus.AUTH_FAIL;
            } else if (e.getMessage().toLowerCase().contains("unknownhostexception")) {
                logger.info("[opencron]:error: DNS Lookup Failed ");
                return Terminal.AuthStatus.HOST_FAIL;
            } else if (e instanceof BadPaddingException) {//RSA解码错误..密码错误...
                return Terminal.AuthStatus.AUTH_FAIL;
            } else {
                return Terminal.AuthStatus.GENERIC_FAIL;
            }
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public PageBean<Terminal> getPageBeanByUser(PageBean pageBean, Long userId) {
        String sql = "SELECT * FROM  T_TERMINAL WHERE USERID = ? ORDER By ";
        pageBean.verifyOrderBy("name", "name", "host", "port", "logintime");
        sql += pageBean.getOrderBy() + " " + pageBean.getOrder();
        return queryDao.getPageBySql(pageBean, Terminal.class, sql, userId);
    }

    public Terminal getById(Long id) {
        return queryDao.get(Terminal.class, id);
    }

    public String delete(HttpSession session, Long id) {
        Terminal term = getById(id);
        if (term == null) {
            return "error";
        }
        User user = OpencronTools.getUser(session);

        if (!OpencronTools.isPermission(session) && !user.getUserId().equals(term.getUserId())) {
            return "error";
        }
        queryDao.createSQLQuery("DELETE FROM T_TERMINAL WHERE id=?", term.getId()).executeUpdate();
        return "success";
    }

    public void login(Terminal terminal) {
        terminal = getById(terminal.getId());
        terminal.setLogintime(new Date());
        queryDao.save(terminal);
    }

    public List<Terminal> getListByUser(User user) {
        String sql = "SELECT * FROM  T_TERMINAL WHERE USERID = ? ";
        return queryDao.sqlQuery(Terminal.class, sql, user.getUserId());
    }

    public void theme(Terminal terminal, String theme) throws Exception {
        terminal.setTheme(theme);
        saveOrUpdate(terminal);
    }

    public static class TerminalClient {

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

        public TerminalClient(WebSocketSession webSocketSession, Terminal terminal) {
            this.webSocketSession = webSocketSession;
            this.terminal = terminal;
            this.httpSessionId = (String) webSocketSession.getAttributes().get(HTTP_SESSION_ID);
            ;
            this.clientId = (String) webSocketSession.getAttributes().get(SSH_SESSION_ID);
            this.sendTempCmdId = this.clientId + this.httpSessionId;
            this.jSch = new JSch();
        }

        public void openTerminal(final int cols, int rows, int width, int height) throws Exception {
            this.session = jSch.getSession(terminal.getUserName(), terminal.getHost(), terminal.getPort());
            this.session.setPassword(terminal.getPassword());

            this.session.setConfig("StrictHostKeyChecking", "no");
            this.session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
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
                                    logger.info("[opencron] Sftp upload file target path:{}", pwd);
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
                    throw new RuntimeException("[opencron] Sftp upload file target path error!");
                }
                dst = dst.replaceFirst("\\./", pwd);
                pwd = null;
            } else if (dst.startsWith("~")) {
                dst = dst.replaceAll("~/|~", "");
            }
            channelSftp.put(file, dst, new OpencronSftpMonitor(fileSize));
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

    public static class TerminalContext implements Serializable {

        //key-->token value--->Terminal
        public static Map<String, Terminal> terminalContext = new ConcurrentHashMap<String, Terminal>(0);

        public static Terminal get(String key) {
            return terminalContext.get(key);
        }

        public static void put(String key, Terminal terminal) {
            //该终端实例只能被的打开一次,之后就失效
            terminalContext.put(key, terminal);
            //保存打开的实例,用于复制终端实例
            OpencronTools.CACHE.put(key, terminal);
        }

        public static Terminal remove(String key) {
            return terminalContext.remove(key);
        }
    }

    public static class TerminalSession implements Serializable {

        //key--->WebSocketSession value--->TerminalClient
        public static Map<WebSocketSession, TerminalClient> terminalSession = new ConcurrentHashMap<WebSocketSession, TerminalClient>(0);

        public static TerminalClient get(WebSocketSession key) {
            return terminalSession.get(key);
        }

        public static TerminalClient get(String key) {
            for (Map.Entry<WebSocketSession, TerminalClient> entry : terminalSession.entrySet()) {
                TerminalClient client = entry.getValue();
                if (client.getClientId().equals(key)) {
                    return client;
                }
            }
            return null;
        }

        public static void put(WebSocketSession key, TerminalClient terminalClient) {
            terminalSession.put(key, terminalClient);
        }

        public static TerminalClient remove(WebSocketSession key) {
            return terminalSession.remove(key);
        }

        public static boolean isOpened(Terminal terminal) {
            for (Map.Entry<WebSocketSession, TerminalClient> entry : terminalSession.entrySet()) {
                if (entry.getValue().getTerminal().equals(terminal)) {
                    return true;
                }
            }
            return false;
        }

        public static List<TerminalClient> findClient(Serializable sessionId) throws IOException {
            List<TerminalClient> terminalClients = new ArrayList<TerminalClient>(0);
            if (notEmpty(terminalSession)) {
                for (Map.Entry<WebSocketSession, TerminalClient> entry : terminalSession.entrySet()) {
                    TerminalClient terminalClient = entry.getValue();
                    if (terminalClient != null && terminalClient.getTerminal() != null) {
                        if (sessionId.equals(terminalClient.getHttpSessionId())) {
                            terminalClients.add(terminalClient);
                        }
                    }
                }
            }
            return terminalClients;
        }

        public static WebSocketSession findSession(Terminal terminal) {
            for (Map.Entry<WebSocketSession, TerminalClient> entry : terminalSession.entrySet()) {
                TerminalClient client = entry.getValue();
                if (client.getTerminal().equals(terminal)) {
                    return entry.getKey();
                }
            }
            return null;
        }

        public static void exit(String httpSessionId) throws IOException {
            if (notEmpty(terminalSession)) {
                for (Map.Entry<WebSocketSession, TerminalClient> entry : terminalSession.entrySet()) {
                    TerminalClient terminalClient = entry.getValue();
                    if (terminalClient.getHttpSessionId().equals(httpSessionId)) {
                        terminalClient.disconnect();
                        terminalClient.getWebSocketSession().sendMessage(new TextMessage("Sorry! Session was invalidated, so opencron Terminal changed to closed. "));
                        terminalClient.getWebSocketSession().close();
                    }
                }
            }
        }

    }

    public static class OpencronSftpMonitor extends TimerTask implements SftpProgressMonitor {

        private long progressInterval = 2 * 1000; // 默认间隔时间为5秒

        private boolean isEnd = false; // 记录传输是否结束

        private long transfered; // 记录已传输的数据总大小

        private long fileSize; // 记录文件总大小

        private Timer timer; // 定时器对象

        private boolean isScheduled = false; // 记录是否已启动timer记时器

        public OpencronSftpMonitor(long fileSize) {
            this.fileSize = fileSize;
        }

        @Override
        public void run() {
            if (!isEnd()) { // 判断传输是否已结束
                long transfered = getTransfered();
                if (transfered != fileSize) { // 判断当前已传输数据大小是否等于文件总大小
                    sendProgressMessage(transfered);
                } else {
                    logger.info("[opencron] Sftp file transfering is done.");
                    setEnd(true); // 如果当前已传输数据大小等于文件总大小，说明已完成，设置end
                }
            } else {
                logger.info("[opencron] Sftp file transfering is done.cancel timer");
                stop(); // 如果传输结束，停止timer记时器
                return;
            }
        }

        public void stop() {
            logger.info("[opencron] Sftp progress monitor Stopping...");
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
                isScheduled = false;
            }
            logger.info("[opencron] Sftp progress monitor Stoped.");
        }

        public void start() {
            logger.info("[opencron] Sftp progress monitor Starting...");
            if (timer == null) {
                timer = new Timer();
            }
            timer.schedule(this, 1000, progressInterval);
            isScheduled = true;
        }

        private void sendProgressMessage(long transfered) {
            if (fileSize != 0) {
                double d = ((double) transfered * 100) / (double) fileSize;
                DecimalFormat df = new DecimalFormat("#.##");
                logger.info("[opencron] Sftp Sending progress message: {} %", df.format(d));
            } else {
                logger.info("[opencron] Sftp Sending progress message: ", transfered);
            }
        }

        public boolean count(long count) {
            if (isEnd()) return false;
            if (!isScheduled) {
                start();
            }
            add(count);
            return true;
        }

        public void end() {
            setEnd(true);
        }

        private synchronized void add(long count) {
            transfered = transfered + count;
        }

        private synchronized long getTransfered() {
            return transfered;
        }

        public synchronized void setTransfered(long transfered) {
            this.transfered = transfered;
        }

        private synchronized void setEnd(boolean isEnd) {
            this.isEnd = isEnd;
        }

        private synchronized boolean isEnd() {
            return isEnd;
        }

        public void init(int op, String src, String dest, long max) {
        }
    }

}


