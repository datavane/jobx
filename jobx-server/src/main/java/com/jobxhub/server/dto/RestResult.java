package com.jobxhub.server.dto;

import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.server.tag.PageBean;

import java.util.HashMap;
import java.util.Map;

public class RestResult {

    private int code;
    private Map<String,Object> body = new HashMap<String,Object>();

    public RestResult(){
    }

    public RestResult(int code,Map<String,Object> body){
        this.code = code;
        this.body = body;
    }

    public static RestResult rest(PageBean<?> pageBean) {
        RestResult result = new RestResult();
        result.setCode(RestStatus.Ok.getStatus());
        result.setBody(pageBean.toMap());
        return result;
    }

    public static RestResult rest(int code) {
        RestResult result = new RestResult();
        result.setCode(code);
        return result;
    }

    public static RestResult rest(int code,Object object) {
        RestResult restResult = rest(code);
        restResult.setBody(CommonUtils.toMap(object));
        return restResult;
    }

    public int getCode() {
        return code;
    }

    public RestResult setCode(int code) {
        this.code = code;
        return this;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public RestResult setBody(Map<String, Object> body) {
        this.body = body;
        return this;
    }

    public RestResult put(String key,Object object){
        this.body.put(key,object);
        return this;
    }
}
