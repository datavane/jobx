package com.jobxhub.server.alarm;
/**
 * @Package com.jobxhub.server.alarm
 * @Title: AlarmMessage
 * @author hitechr
 * @date 2018/6/12 15:29
 * @version V1.0
 */

import com.jobxhub.common.job.Alarm;
import com.jobxhub.server.dto.Job;

/**
 * @Descriptions:
 */
public class AlarmMessage {



    private Alarm.AlarmType alarmType;
    private Alarm.AlarmCode alarmCode;

    private String msg;
    private String mobiles;
    private String email;
    private String weixin;


    public AlarmMessage(Job job){
        this.email= job.getEmail();
        this.mobiles=job.getMobile();
        this.alarmCode=Alarm.getAlarmCode(job.getAlarmCode());
        this.alarmType=Alarm.getAlarmType(job.getAlarmType());

    }

    public AlarmMessage(String msg, String mobiles, String email, String weixin,Alarm.AlarmType alarmType,Alarm.AlarmCode alarmCode) {
        this.msg = msg;
        this.mobiles = mobiles;
        this.email = email;
        this.weixin = weixin;
        this.alarmCode=alarmCode;
        this.alarmType=alarmType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMobiles() {
        return mobiles;
    }

    public void setMobiles(String mobiles) {
        this.mobiles = mobiles;
    }

    public Alarm.AlarmType getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(Alarm.AlarmType alarmType) {
        this.alarmType = alarmType;
    }

    public Alarm.AlarmCode getAlarmCode() {
        return alarmCode;
    }

    public void setAlarmCode(Alarm.AlarmCode alarmCode) {
        this.alarmCode = alarmCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }
}
