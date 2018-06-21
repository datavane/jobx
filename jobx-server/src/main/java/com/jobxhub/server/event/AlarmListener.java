package com.jobxhub.server.event;
/**
 * @Package com.jobxhub.server.event
 * @Title: AlarmListener
 * @author hitechr
 * @date 2018/6/11 9:01
 * @version V1.0
 */

import com.jobxhub.common.job.Alarm;
import com.jobxhub.server.alarm.AlarmMessage;
import com.jobxhub.server.alarm.AlarmNoticeFacory;
import com.jobxhub.server.alarm.SendNotice;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * @Descriptions: 专门发送通知的监听器
 */
@EnableAsync
@Component
public class AlarmListener implements ApplicationListener<AlarmEvent> {

    @Override
    @Async
    public void onApplicationEvent(AlarmEvent alarmEvent) {
        AlarmMessage alarmMessage = alarmEvent.getAlarmMessage();
        Alarm.AlarmType alarmType = alarmMessage.getAlarmType();//获取通知方式
        int[] scatter = alarmType.scatter();
        for(int code:scatter){
            SendNotice instantce = AlarmNoticeFacory.getInstantce(Alarm.getAlarmType(code));
            instantce.send(null);
        }
    }

    public static void main(String[] args) {
        int a=25;
        String s = Integer.toBinaryString(a);
        int length = s.replaceAll("0", "").length();
        char[] chars = s.toCharArray();
        int[] arrs= new int[length];
        for(int i=0,j=0;i<chars.length;i++){
            if(chars[i]=='1'){
                arrs[j++]=1<<chars.length-i-1;
            }
        }
        for(int b:arrs){
            System.out.println(b);
        }

    }


}
