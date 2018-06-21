package com.jobxhub.server.alarm;
/**
 * @Package com.jobxhub.server.alarm
 * @Title: SendNotice
 * @author hitechr
 * @date 2018/6/10 19:17
 * @version V1.0
 */

/**
 * @Descriptions: 发送消息的接口
 */
public interface SendNotice {

    void send(AlarmMessage message);

}
