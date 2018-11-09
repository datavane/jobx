/**
 * Copyright (c) 2015 The JobX Project
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jobxhub.service.vo;

import com.jobxhub.common.util.collection.HashMap;

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

    public static RestResult ok() {
        RestResult restResult = rest(200);
        return restResult;
    }

    public static RestResult ok(Object body) {
        RestResult restResult = rest(200);
        restResult.setBody(body);
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
