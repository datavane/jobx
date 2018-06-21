package com.jobxhub.common.util;
/**
 * @Package com.jobxhub.common.util
 * @Title: EnumUtil
 * @author hitechr
 * @date 2018/6/12 16:43
 * @version V1.0
 */

import com.jobxhub.common.job.Alarm;

/**
 * @Descriptions: 枚举工具类
 */
public class EnumUtil {

    public interface CommonEnum{
        int getCode();
    }

    /**
     * 根据code码获取
     * @param clazz
     * @param code
     * @param <T>
     * @return
     */
    public static <T extends CommonEnum> T getEnumBycode(Class<T> clazz, int code){
        for(T enu:clazz.getEnumConstants()){
            if(code==enu.getCode()){
                return enu;
            }
        }
        return null;
    }


    public static void main(String[] args) {
        Alarm.AlarmType enumBycode = EnumUtil.getEnumBycode(Alarm.AlarmType.class, 8);
        System.out.println(enumBycode);
    }

}
