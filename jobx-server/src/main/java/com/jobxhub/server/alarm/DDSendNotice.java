package com.jobxhub.server.alarm;
/**
 * @Package com.jobxhub.server.alarm
 * @Title: DDSendNotice
 * @author hitechr
 * @date 2018/6/12 15:09
 * @version V1.0
 */

import org.springframework.stereotype.Service;

/**
 * @Descriptions:
 */
@Service("ddSendNotice")
public class DDSendNotice extends AbstractSendNotice {

    String url="https://oapi.dingtalk.com/robot/send?access_token=cffcc6996a57a834a8bde29b79f4ae77103eb57fa1ce37f2743e1867be03dd87";



    @Override
    public void send(AlarmMessage message) {


    }

}
