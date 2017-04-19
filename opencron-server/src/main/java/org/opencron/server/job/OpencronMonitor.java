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

package org.opencron.server.job;

import org.opencron.common.utils.CommonUtils;
import org.opencron.common.utils.HttpUtils;
import org.opencron.server.domain.Agent;
import org.opencron.server.service.AgentService;
import org.opencron.server.service.ConfigService;
import org.opencron.server.service.ExecuteService;
import org.opencron.server.service.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by benjobs on 2016/12/15.
 */

@Component
public class OpencronMonitor implements Serializable{

    private final Logger logger = LoggerFactory.getLogger(OpencronMonitor.class);

    public static int port;

    private volatile boolean running = false;

    private ConcurrentHashMap<Class, ObjectAction> actionMapping = new ConcurrentHashMap<Class, ObjectAction>();

    private Map<Agent, Long> successConnStatus = new ConcurrentHashMap<Agent, Long>(0);

    private Map<String, Agent> agentMap = new ConcurrentHashMap<String, Agent>(0);

    private Thread connWatchDog;

    private long keepAliveDelay = 1000 * 10;//10秒一次心跳

    @Autowired
    private AgentService agentService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private ExecuteService executeService;

    /**
     * 要处理客户端发来的对象，并返回一个对象，可实现该接口。
     */
    public interface ObjectAction {
        void doAction(Object rev);
    }

    public final class DefaultObjectAction implements ObjectAction {
        public void doAction(Object rev) {
            String ip = rev.toString();
            if (agentMap.get(ip) == null) {
                Agent agent = agentService.getByHost(rev.toString());
                agentMap.put(ip, agent);
            }
            successConnStatus.put(agentMap.get(ip), System.currentTimeMillis());
        }
    }

    public void start() {
        logger.info("[opencron]:checking Agent connection...");
        this.port = HttpUtils.freePort();
        if (running) return;
        running = true;
        connWatchDog = new Thread(new ConnWatchDog());
        connWatchDog.start();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                List<Agent> agents = agentService.getAll();
                if (agents.size() != successConnStatus.size()) {
                    for (Agent agent : agents) {
                        if (successConnStatus.get(agent) == null) {
                            boolean ping = executeService.ping(agent);
                            //ping失败...
                            if(!ping) {
                                if (CommonUtils.isEmpty(agent.getFailTime()) || new Date().getTime() - agent.getFailTime().getTime() >= configService.getSysConfig().getSpaceTime() * 60 * 1000) {
                                    noticeService.notice(agent);
                                    //记录本次任务失败的时间
                                    agent.setFailTime(new Date());
                                    agent.setStatus(false);
                                    agentService.addOrUpdate(agent);
                                }else if (agent.getStatus()) {
                                    agent.setStatus(false);
                                    agentService.addOrUpdate(agent);
                                }
                            }
                        }
                    }
                }

                for (Map.Entry<Agent, Long> entry : successConnStatus.entrySet()) {
                    long lastAliveTime = entry.getValue();
                    Agent agent = entry.getKey();
                    //已经失联的状态,再次通知连接
                    if (!agent.getStatus()) {
                        boolean ping = executeService.ping(agent);
                        if (ping) {
                            agent.setStatus(true);
                            agentService.addOrUpdate(agent);
                            continue;
                        }
                    }
                    if (System.currentTimeMillis() - lastAliveTime > OpencronMonitor.this.keepAliveDelay) {
                        if (CommonUtils.isEmpty(agent.getFailTime()) || new Date().getTime() - agent.getFailTime().getTime() >= configService.getSysConfig().getSpaceTime() * 60 * 1000) {
                            noticeService.notice(agent);
                            //记录本次任务失败的时间
                            agent.setFailTime(new Date());
                            agent.setStatus(false);
                            agentService.addOrUpdate(agent);
                        }
                        if (agent.getStatus()) {
                            agent.setStatus(false);
                            agentService.addOrUpdate(agent);
                        }
                    } else {
                        if (!agent.getStatus()) {
                            agent.setStatus(true);
                            agentService.addOrUpdate(agent);
                        }
                    }
                }
            }
        }, 0, keepAliveDelay);

    }

    @SuppressWarnings("deprecation")
    public void stop() {
        if (running) running = false;
        if (connWatchDog != null) connWatchDog.stop();
    }

    class ConnWatchDog implements Runnable {
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(port, 5);
                while (running) {
                    Socket socket = serverSocket.accept();
                    new Thread(new SocketAction(socket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
                OpencronMonitor.this.stop();
            }
        }
    }

    class SocketAction implements Runnable {

        private Socket socket;

        private boolean run = true;

        public SocketAction(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            while (running && run) {
                try {
                    InputStream in = socket.getInputStream();
                    if (in.available() > 0) {
                        ObjectInputStream inputStream = new ObjectInputStream(in);
                        Object obj = inputStream.readObject();
                        ObjectAction action = actionMapping.get(obj.getClass());
                        action = action == null ? new DefaultObjectAction() : action;
                        action.doAction(obj);
                    } else {
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
