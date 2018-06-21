package com.jobxhub.server.alarm;
/**
 * @Package com.jobxhub.server.alarm
 * @Title: AbstractSendNotice
 * @author hitechr
 * @date 2018/6/12 9:39
 * @version V1.0
 */

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.jobxhub.common.util.collection.HashMap;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Descriptions:
 */
public abstract class AbstractSendNotice implements SendNotice {

    private boolean httpPostSend(int successCode,String urlPrefix, String content) {
        URL url = null;
        InputStream in = null;
        OutputStreamWriter writer = null;
        HttpURLConnection conn = null;

        try {
            url = new URL(urlPrefix);
            conn = (HttpURLConnection)url.openConnection();

            conn.setConnectTimeout(2000);
            conn.setReadTimeout(3000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("content-type", "application/json;charset=utf-8");
//            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=UTF-8");
            writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write(content);
            writer.flush();

            in = conn.getInputStream();
            System.out.println(IOUtils.toString(in,"UTF-8"));
            int responseCode = conn.getResponseCode();
            return successCode==responseCode;
//            System.out.println(responseCode);
//
//            StringBuilder sb = new StringBuilder();
//            sb.append(IOUtils.toString(in,"UTF-8"));

        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
            }
        }
        return false;
    }


    private boolean httpGetSend(int successCode,String urlPrefix, String urlPars) {
        URL url = null;
        InputStream in = null;
        HttpURLConnection conn = null;

        try {
            url = new URL(urlPrefix + "?" + urlPars);
            conn = (HttpURLConnection)url.openConnection();

            conn.setConnectTimeout(2000);
            conn.setReadTimeout(3000);
            int responseCode = conn.getResponseCode();
            return successCode==responseCode;

//            in = conn.getInputStream();
//            StringBuilder sb = new StringBuilder();
//            sb.append(IOUtils.toString(in,"UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        }



        return false;
    }


    public boolean httpSend(int successCode, String type, String urlPrefix, String urlPars) {
        if ("get".equalsIgnoreCase(type)) {
            return httpGetSend(successCode, urlPrefix, urlPars);
        } else if ("post".equalsIgnoreCase(type)) {
            return httpPostSend(successCode, urlPrefix, urlPars);
        }
        return false;
    }
    public static void m23ain(String[] args) {
        List<String> list= new ArrayList<>();
        list.add("12121");
        list.add("27323");
        System.out.println(list);
    }


    public static void main(String[] args) {
        ;
        JSONObject jsonObject= new JSONObject();
        jsonObject.put("msgtype","text");
        JSONObject contest= new JSONObject();
        contest.put("content","test");
        jsonObject.put("text",contest.toJSONString());
        JSONObject mobile= new JSONObject();
        List<String> list= new ArrayList<>();
        list.add("1825718XXXX");
        mobile.put("atMobiles",list);
        mobile.put("isAtAll",false);
        jsonObject.put("at",mobile.toJSONString());
        System.out.println(jsonObject.toJSONString());

        Map<String,String> map= new HashMap<>();
        map.put("msgtype","text");

        String msg="{" +
                "     'msgtype': 'test'," +
                "     'text': {" +
                "         'content': '我就是我,  @1825718XXXX 是不一样的烟火'" +
                "     }," +
                "     'at': {" +
                "         'atMobiles': [" +
                "             '1825718XXXX'" +
                "         ], " +
                "         'isAtAll': false" +
                "     }" +
                " }";

//        AbstractSendNotice notice= new AbstractSendNotice();
//        String url="https://oapi.dingtalk.com/robot/send?access_token=cffcc6996a57a834a8bde29b79f4ae77103eb57fa1ce37f2743e1867be03dd87";
//        boolean b = notice.httpPostSend(200, url, jsonObject.toJSONString());
//        System.out.println(b);
    }

    public String getDDMsg(String content,String... mobiles){
        JSONObject jsonObject= new JSONObject();
        jsonObject.put("msgtype","text");
        JSONObject contest= new JSONObject();


        JSONObject mobile= new JSONObject();
        StringBuffer sb= new StringBuffer(content);

        if(mobiles!=null && mobiles.length>0){
            List<String> list= new ArrayList<>();
            for(String m:mobiles){
                list.add(m);
                sb.append("@"+m+";");
            }
            mobile.put("atMobiles",list);
        }else{
            mobile.put("isAtAll",false);
        }

        contest.put("content",sb.toString());

        jsonObject.put("text",contest.toJSONString());
        jsonObject.put("at",mobile.toJSONString());
        return jsonObject.toJSONString();
    }
}


