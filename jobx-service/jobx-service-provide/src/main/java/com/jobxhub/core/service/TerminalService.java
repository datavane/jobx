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

package com.jobxhub.core.service;

import com.google.common.collect.Lists;
import com.jcraft.jsch.*;
import com.jobxhub.common.Constants;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.IOUtils;
import com.jobxhub.core.entity.TerminalEntity;
import com.jobxhub.core.dao.TerminalDao;
import com.jobxhub.core.support.JobXTools;
import com.jobxhub.core.support.SshUserInfo;
import com.jobxhub.core.support.TerminalClient;
import com.jobxhub.core.tag.PageBean;
import com.jobxhub.core.model.Terminal;
import com.jobxhub.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

import static com.jobxhub.common.util.CommonUtils.notEmpty;

/**
 * @author <a href="mailto:benjobs@qq.com">benjobs@qq.com</a>
 * @name:CommonUtil
 * @version: 1.0.0
 * @company: com.jobxhub
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
    private TerminalDao terminalDao;

    public boolean exists(String userName, String host) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>(0);
        map.put("user_name", userName);
        map.put("host", host);
        int count = terminalDao.getCount(map);
        return count > 0;
    }

    public boolean merge(Terminal terminal) throws Exception {
        try {
            TerminalEntity terminalEntity = Terminal.transferEntity.apply(terminal);
            if (terminalEntity.getId() == null) {
                terminalDao.save(terminalEntity);
                terminal.setId(terminalEntity.getId());
            } else {
                terminalDao.update(terminalEntity);
            }
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
            Constants.SshType sshType = Constants.SshType.getByType(terminal.getSshType());
            switch (sshType) {
                case SSHKEY:
                    //需要读取用户上传的sshKey
                    if (terminal.getSshKeyFile() != null) {
                        //将keyfile读取到数据库
                        terminal.setPrivateKey(terminal.getSshKeyFile().getBytes());
                    }
                    if (notEmpty(terminal.getPrivateKey())) {
                        File keyFile = new File(terminal.getPrivateKeyPath());
                        if (keyFile.exists()) {
                            keyFile.delete();
                        }
                        //将数据库中的私钥写到用户的机器上
                        IOUtils.writeFile(keyFile, new ByteArrayInputStream(terminal.getPrivateKey()));
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
                    break;
                case ACCOUNT:
                    session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
                    session.setPassword(terminal.getPassword());
                    break;
            }
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(TerminalClient.SESSION_TIMEOUT);
            return Terminal.AuthStatus.SUCCESS;
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().contains("userauth fail")) {
                return Terminal.AuthStatus.PUBLIC_KEY_FAIL;
            } else if (e.getMessage().toLowerCase().contains("auth fail") || e.getMessage().toLowerCase().contains("auth cancel")) {
                return Terminal.AuthStatus.AUTH_FAIL;
            } else if (e.getMessage().toLowerCase().contains("unknownhostexception")) {
                if (logger.isInfoEnabled()) {
                    logger.info("[JobX]:error: DNS Lookup Failed ");
                }
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

    public void getPageBean(PageBean pageBean, Long userId) {
        pageBean.verifyOrderBy("name", "name", "host", "port", "ssh_type", "login_time");
        pageBean.put("user_id", userId);

        List<TerminalEntity> beanList = terminalDao.getByPageBean(pageBean);
        if (CommonUtils.notEmpty(beanList)) {
            int count = terminalDao.getCount(pageBean.getFilter());
            pageBean.setResult(beanList);
            pageBean.setTotalRecord(count);
        }
    }

    public Terminal getById(Long id) {
        TerminalEntity terminalEntity = terminalDao.getById(id);
        return Terminal.transferModel.apply(terminalEntity);
    }

    public String delete(HttpSession session, Long id) {
        Terminal term = getById(id);
        if (term == null) {
            return "error";
        }
        User user = JobXTools.getUser(session);

        if (!JobXTools.isPermission(session) && !user.getUserId().equals(term.getUserId())) {
            return "error";
        }
        terminalDao.delete(id);
        return "true";
    }

    public void login(Long id) {
        Date date = new Date();
        terminalDao.updateLoginTime(id,date);
    }

    public List<Terminal> getByUser(Long userId) {
        List<TerminalEntity> list = terminalDao.getByUser(userId);
        return Lists.transform(list, Terminal.transferModel);
    }

    public void theme(Terminal terminal, String theme) throws Exception {
        if (terminal != null) {
            terminalDao.updateTheme(terminal.getId(), theme);
        }
    }


}


