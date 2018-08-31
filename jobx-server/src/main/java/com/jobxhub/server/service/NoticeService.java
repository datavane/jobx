/**
 * Copyright (c) 2015 The JobX Project
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


package com.jobxhub.server.service;

import com.jobxhub.common.Constants;
import com.jobxhub.common.job.Alarm;
import com.jobxhub.common.util.EnumUtil;
import com.jobxhub.server.alarm.AlarmMessage;
import com.jobxhub.server.dto.*;
import com.jobxhub.server.domain.UserBean;
import com.jobxhub.server.event.AlarmEvent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.mail.HtmlEmail;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.DateUtils;
import com.jobxhub.common.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.*;
import static com.jobxhub.common.job.Alarm.auth;
import static com.jobxhub.common.job.Alarm.AlarmCode.*;

/**
 * Created by benjobs on 16/3/18.
 */
@Service
public class NoticeService {

    @Autowired
    private ConfigService configService;


    @Autowired
    private LogService logService;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private ApplicationContext applicationContext;

    private Template template;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void initConfig() throws Exception {
        Configuration configuration = new Configuration();
        File file = new File(servletContext.getRealPath("/WEB-INF/layouts"));
        configuration.setDirectoryForTemplateLoading(file);
        configuration.setDefaultEncoding("UTF-8");
        this.template = configuration.getTemplate("email.html");
    }

    public void notice(Agent agent) {
        if (!agent.getWarning()) return;
        String content = getMessage(agent, "通信失败,请速速处理!");
        if (logger.isInfoEnabled()) {
            logger.info(content);
        }
        try {
            sendMessage(agent.getUsers(), agent.getAgentId(), agent.getEmail(), agent.getMobile(), content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notice(Job job, String msg) {
        if (!job.getWarning()) return;

        //当前的单一任务只运行一次未设置重跑.
        if (job.getRedo() == 0 || job.getRunCount() == 0) {
            Agent agent = job.getAgent();
            String message = "执行任务:" + job.getCommand() + "(" + job.getCronExp() + ")失败,%s!";
            if (msg == null) {
                message = String.format(message, "");
            } else {
                message = String.format(message, "[" + msg + "]");
            }
            String content = getMessage(agent, message);
            if (logger.isInfoEnabled()) {
                logger.info(content);
            }
            try {
                sendMessage(null, agent.getAgentId(),job.getEmail(), job.getMobile(),content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getMessage(Agent agent, String message) {
        String msgFormat = "[JobX] 机器:%s(%s:%s)%s\n\r\t\t--%s";
        return String.format(msgFormat, agent.getName(), agent.getHost(), agent.getPort(), message, DateUtils.formatFullDate(new Date()));
    }

    public void sendMessage(List<UserBean> users, Long workId, String email,String mobile, String content) {
        Log log = new Log();
        log.setRead(false);
        log.setAgentId(workId);
        log.setMessage(content);
        log.setSendTime(new Date());

        Config config = configService.getSysConfig();

        //发送邮件
        if (CommonUtils.notEmpty(email)) {
            try {
                log.setType(Constants.MsgType.EMAIL.getValue());
                HtmlEmail htmlEmail = new HtmlEmail();
                htmlEmail.setCharset("UTF-8");
                htmlEmail.setHostName(config.getSmtpHost());
                htmlEmail.setSSLOnConnect(true);
                htmlEmail.setSslSmtpPort(config.getSmtpPort().toString());
                htmlEmail.setAuthentication(config.getSenderEmail(), config.getEmailPassword());
                htmlEmail.setFrom(config.getSenderEmail());
                htmlEmail.setSubject("jobx监控告警");
                htmlEmail.setHtmlMsg(msgToHtml(content));
                htmlEmail.addTo(email.split(","));
                htmlEmail.send();
                log.setReceiver(email);
                logService.save(log);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        //发送短信
        try {
            for (String _mobile : mobile.split(",")) {
                //发送POST请求
                String sendUrl = String.format(config.getSendUrl(), _mobile, String.format(config.getTemplate(), content));
                String url = sendUrl.substring(0, sendUrl.indexOf("?"));
                String postData = sendUrl.substring(sendUrl.indexOf("?") + 1);
                String message = HttpUtils.doPost(url, postData, "UTF-8");
                log.setResult(message);
                if (logger.isInfoEnabled()) {
                    logger.info(message);
                }
                log.setReceiver(_mobile);
                log.setType(Constants.MsgType.SMS.getValue());
                log.setSendTime(new Date());
                logService.save(log);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        //发送站内信
        log.setType(Constants.MsgType.WEBSITE.getValue());
        for (UserBean user : users) {
            //一一发送站内信
            log.setUserId(user.getUserId());
            log.setReceiver(user.getUserName());
            logService.save(log);
        }

    }

    private String msgToHtml(String content) throws Exception {
        Map root = new HashMap();
        root.put("message", content);
        StringWriter writer = new StringWriter();
        template.process(root, writer);
        return writer.toString();
    }

    /**
     * 根据作业的执行结果进行通知
     * @param job
     * @param record
     */
    public void notice2(Job job, Record record) {

        AlarmMessage alarmMessage=new AlarmMessage(job);
        Integer success = record.getSuccess();//成功，失败
        Constants.ResultStatus resultStatus = EnumUtil.getEnumBycode(Constants.ResultStatus.class, success);
        Integer alarmCode = job.getAlarmCode();
        boolean notice=false;
        switch (resultStatus){
            case FAILED ://失败
                if(auth(alarmCode,FAIL.getCode())){//如果是失败通知
                    notice=true;
                }
                System.out.println("ok");
                break;
            case SUCCESSFUL://成功
                if(auth(alarmCode,SUCCESS.getCode())){//如果是成功通知
                    notice=true;
                }
                break;
            case TIMEOUT://超时
                if(auth(alarmCode,TIMEOUT.getCode())){//如果是超时通知
                    notice=true;
                }
                break;
            default:
                return;
        }
        if(notice){
            AlarmEvent alarmEvent= new AlarmEvent(this,alarmMessage);
            applicationContext.publishEvent(alarmEvent);
        }
    }
}
