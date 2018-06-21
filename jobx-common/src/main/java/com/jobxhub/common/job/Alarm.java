package com.jobxhub.common.job;
/**
 * @Package com.jobxhub.common.job
 * @Title: Alarm
 * @author hitechr
 * @date 2018/6/10 10:13
 * @version V1.0
 */

import com.jobxhub.common.Constants;
import com.jobxhub.common.util.EnumUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Descriptions: 告警相关
 */
public class Alarm {

    static Map<Integer,AlarmType> alarmTypeMap= new HashMap<>();
    static Map<Integer,AlarmCode> alarmCodeMap= new HashMap<>();

    static {
        AlarmCode[] alarmCodes = AlarmCode.values();
        for(AlarmCode ac:alarmCodes){
            alarmCodeMap.put(ac.getCode(),ac);
        }

        AlarmType[] alarmTypes = AlarmType.values();
        for(AlarmType at:alarmTypes){
            alarmTypeMap.put(at.getCode(),at);
        }

    }
    /**
     * 根据通知类型获取对应泛型
     * @param code
     * @return
     */
    public static AlarmType getAlarmType(int code){
        return alarmTypeMap.get(code);
    }
    public static AlarmCode getAlarmCode(int code){
        return alarmCodeMap.get(code);
    }

    /**
     * 告警码
     */
    public enum  AlarmCode{
        FAIL(0),
        TIMEOUT(1),
        SUCCESS(2),
        ERROR(3);
        int code;

        AlarmCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return bit(code);
        }
        public int[] scatter(){
            return Alarm.scatter(this.getCode());
        }
    }

    /**
     * 告警方式
     */
    public enum  AlarmType implements EnumUtil.CommonEnum{

        SMS(Constants.MsgType.SMS.getValue()),
        MAIL(Constants.MsgType.EMAIL.getValue()),
        WEBSITE(Constants.MsgType.WEBSITE.getValue()),
        DingDing(3),
        WeiXin(4);
        int code;

        AlarmType(int code) {
            this.code = code;
        }

        public int getCode() {
            return bit(code);
        }
        public int[] scatter(){
            return Alarm.scatter(this.getCode());
        }

    }

    private static int bit(int code){
        return 1<<code;
    }


    /**
     *
     * @param code 授权码
     * @param num 权限
     * @return
     */
    public static boolean auth(int code,int num){
        return num == (code & num);
    }

    /**
     * 获取授权码
     * @param nums
     * @return
     */
    public static int code(Integer[] nums){
        if(nums==null || nums.length<1){
            return 0;
        }
        int code=0;
        for(Integer num:nums){
            code=code|num;
        }
        return code;
    }
    public static int[] scatter(int code){
        String s = Integer.toBinaryString(code);
        int length = s.replaceAll("0", "").length();
        char[] chars = s.toCharArray();
        int[] arrs= new int[length];
        for(int i=0,j=0;i<chars.length;i++){
            if(chars[i]=='1'){
                arrs[j++]=1<<chars.length-i-1;
            }
        }
        return arrs;
    }


    public static void main(String[] args) {

        AlarmType[] alarmTypes = AlarmType.values();
        for(AlarmType at:alarmTypes){
            System.out.println(at+" "+Arrays.toString(at.scatter()));
        }

        AlarmCode[] alarmCodes = AlarmCode.values();
        for(AlarmCode ac:alarmCodes){
            System.out.println(ac+" "+Arrays.toString(ac.scatter()));
        }


    }



}
