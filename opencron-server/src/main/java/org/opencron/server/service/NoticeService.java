/**
 * Copyright 2016 benjobs
 * <p/>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.opencron.server.service;

import org.opencron.common.job.Opencron;
import org.opencron.server.domain.Config;
import org.opencron.server.domain.Log;
import org.opencron.server.domain.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.mail.HtmlEmail;
import org.opencron.common.utils.CommonUtils;
import org.opencron.common.utils.DateUtils;
import org.opencron.common.utils.HttpUtils;
import org.opencron.server.domain.Agent;
import org.opencron.server.vo.JobVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.*;

/**
 * Created by benjobs on 16/3/18.
 */
@Service
public class NoticeService {

    @Autowired
    private ConfigService configService;

    @Autowired
    private HomeService homeService;

    @Autowired
    private ServletContext servletContext;

    private Template template;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void initConfig() throws Exception {
        Configuration configuration = new Configuration();
        File file = new File(servletContext.getRealPath("/WEB-INF/layouts"));
        configuration.setDirectoryForTemplateLoading(file);
        configuration.setDefaultEncoding("UTF-8");
        this.template = configuration.getTemplate("email.template");
    }

    public void notice(Agent agent) {
        if (!agent.getWarning()) return;
        String content = getMessage(agent, "通信失败,请速速处理!");
        logger.info(content);
        try {
            sendMessage(agent.getUsers(), agent.getAgentId(), agent.getEmailAddress(), agent.getMobiles(), content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notice(JobVo job,String msg) {
        if (!job.getWarning()) return;
        Agent agent = job.getAgent();
        String message = "执行任务:" + job.getCommand() + "(" + job.getCronExp() + ")失败,%s!";
        if (msg==null) {
            message = String.format(message,"");
        }else {
            message = String.format(message,"["+msg+"]");
        }
        String content = getMessage(agent,message);
        logger.info(content);
        try {
            sendMessage(Arrays.asList(job.getUser()),agent.getAgentId(), job.getEmailAddress(), job.getMobiles(), content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMessage(Agent agent, String message) {
        String msgFormat = "[opencron] 机器:%s(%s:%s)%s\n\r\t\t--%s";
        return String.format(msgFormat, agent.getName(), agent.getIp(), agent.getPort(), message, DateUtils.formatFullDate(new Date()));
    }

    public void sendMessage(List<User> users,Long workId, String emailAddress, String mobiles, String content) {
        Log log = new Log();
        log.setIsread(false);
        log.setAgentId(workId);
        log.setMessage(content);
        log.setSendTime(new Date());

        Config config = configService.getSysConfig();

        //发送邮件
        if (CommonUtils.notEmpty(emailAddress)) {
            try {
                log.setType(Opencron.MsgType.EMAIL.getValue());
                HtmlEmail email = new HtmlEmail();
                email.setCharset("UTF-8");
                email.setHostName(config.getSmtpHost());
                email.setSslSmtpPort(config.getSmtpPort().toString());
                email.setAuthentication(config.getSenderEmail(), config.getPassword());
                email.setFrom(config.getSenderEmail());
                email.setSubject("opencron监控告警");
                email.setHtmlMsg(msgToHtml(content));
                email.addTo(emailAddress.split(","));
                email.send();
                log.setReceiver(emailAddress);
                homeService.saveLog(log);
            }catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        //发送短信
        try {
            for (String mobile : mobiles.split(",")) {
                //发送POST请求
                String sendUrl = String.format(config.getSendUrl(), mobile, String.format(config.getTemplate(), content));
                String url = sendUrl.substring(0, sendUrl.indexOf("?"));
                String postData = sendUrl.substring(sendUrl.indexOf("?") + 1);
                String message = HttpUtils.doPost(url, postData, "UTF-8");
                log.setResult(message);
                logger.info(message);
                log.setReceiver(mobiles);
                log.setType(Opencron.MsgType.SMS.getValue());
                log.setSendTime(new Date());
                homeService.saveLog(log);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        //发送站内信
        log.setType(Opencron.MsgType.WEBSITE.getValue());
        for(User user:users) {
            //一一发送站内信
            log.setUserId(user.getUserId());
            log.setReceiver(user.getUserName());
            homeService.saveLog(log);
        }


    }

    private String msgToHtml(String content) throws Exception {
        Map root = new HashMap();
        root.put("message", content);
        StringWriter writer = new StringWriter();
        template.process(root, writer);
        return writer.toString();
    }


}
