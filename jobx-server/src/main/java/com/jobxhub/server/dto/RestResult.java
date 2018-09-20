package com.jobxhub.server.dto;

import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.collection.HashMap;
import com.jobxhub.server.tag.PageBean;

import java.util.Map;

public class RestResult {

    private int code;
    private Object body;

    public RestResult(){
    }

    public RestResult(int code,Object body){
        this.code = code;
        this.body = body;
    }

    public static RestResult rest(PageBean<?> pageBean) {
        RestResult result = new RestResult();
        result.setCode(RestStatus.Ok.getStatus());
        result.setBody(pageBean);
        return result;
    }

    public static RestResult rest(int code) {
        RestResult result = new RestResult();
        result.setCode(code);
        return result;
    }

    public static RestResult rest(int code,Object object) {
        RestResult restResult = rest(code);
        restResult.setBody(object);
        return restResult;
    }

    public int getCode() {
        return code;
    }

    public RestResult setCode(int code) {
        this.code = code;
        return this;
    }

    public Object getBody() {
        return body;
    }

    public RestResult setBody(Object body) {
        this.body = body;
        return this;
    }

    public RestResult put(String key,Object object){
        if (this.body instanceof Map) {
            ((Map) this.body).put(key,object);
        }else {
            Map<String,Object> map = new HashMap<String,Object>();
            map.put(key,object);
            this.body = map;
        }
        return this;
    }
}
