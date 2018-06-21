package com.jobxhub.server.event;
/**
 * @Package com.jobxhub.server.event
 * @Title: AlarmEvent
 * @author hitechr
 * @date 2018/6/11 8:58
 * @version V1.0
 */

import com.jobxhub.server.alarm.AlarmMessage;
import org.springframework.context.ApplicationEvent;

/**
 * @Descriptions: 发送消息的事件
 */
public class AlarmEvent extends ApplicationEvent {


    private AlarmMessage alarmMessage;


    public AlarmEvent(Object source, AlarmMessage alarmMessage) {
        super(source);
        this.alarmMessage = alarmMessage;
    }

    public AlarmMessage getAlarmMessage() {
        return alarmMessage;
    }

    public void setAlarmMessage(AlarmMessage alarmMessage) {
        this.alarmMessage = alarmMessage;
    }
}
