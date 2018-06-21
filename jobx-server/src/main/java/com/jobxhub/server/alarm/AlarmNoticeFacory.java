package com.jobxhub.server.alarm;
/**
 * @Package com.jobxhub.server.alarm
 * @Title: AlarmNoticeFacory
 * @author hitechr
 * @date 2018/6/12 17:21
 * @version V1.0
 */

import com.jobxhub.common.job.Alarm;

import java.util.Map;

/**
 * @Descriptions:
 */
public class AlarmNoticeFacory {

    static Map<Object,SendNotice> sendNoticeMap;

    public static SendNotice getInstantce(Alarm.AlarmType alarmType ){
        return sendNoticeMap.get(alarmType.getCode());
    }

    public void setSendNoticeMap(Map<Object, SendNotice> sendNoticeMap) {
        AlarmNoticeFacory.sendNoticeMap = sendNoticeMap;
    }
}
