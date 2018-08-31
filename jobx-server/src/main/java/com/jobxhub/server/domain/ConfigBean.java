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

package com.jobxhub.server.domain;

import com.jobxhub.common.util.StringUtils;
import com.jobxhub.server.dto.Config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChenHui on 2016/2/17.
 */
public class ConfigBean {

    private String configKey;
    private String configVal;
    private String comment;

    public ConfigBean() {}

    public static List<ConfigBean> transform(Config config) {
        Field[] fields = Config.class.getDeclaredFields();
        List<ConfigBean> configBeans = new ArrayList<ConfigBean>(0);
        for (Field field:fields) {
            try {
                field.setAccessible(true);
                ConfigBean configBean = new ConfigBean();
                if (field.getName().equals("useSSL")) {
                    configBean.setConfigKey("use_ssl");
                }else {
                    configBean.setConfigKey(StringUtils.camelToSplitName(field.getName(),"_"));
                }
                Object val = field.get(config);
                if (val!=null) {
                    configBean.setConfigVal(val.toString());
                }
                configBeans.add(configBean);
            }catch (Exception e) {

            }
        }
        return configBeans;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigVal() {
        return configVal;
    }

    public void setConfigVal(String configVal) {
        this.configVal = configVal;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}