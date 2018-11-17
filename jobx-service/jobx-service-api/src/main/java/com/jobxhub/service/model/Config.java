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

package com.jobxhub.service.model;


import com.jobxhub.common.util.StringUtils;
import com.jobxhub.service.entity.ConfigEntity;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class Config implements Serializable {

    private String smtpHost;
    private Integer smtpPort;
    private Boolean useSsl;
    private String senderEmail;
    private String emailPassword;
    private String sendUrl;
    private Integer spaceTime;
    private String template;
    private String execUser;
    private String version;

    public static List<ConfigEntity> toEntity(Config config) {
        Field[] fields = Config.class.getDeclaredFields();
        List<ConfigEntity> configBeans = new ArrayList<ConfigEntity>(0);
        for (Field field:fields) {
            try {
                field.setAccessible(true);
                ConfigEntity configBean = new ConfigEntity();
                configBean.setConfigKey(StringUtils.camelToSplitName(field.getName(),"_"));
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

    public void fromEntity(ConfigEntity config) {
        Field[] fields = Config.class.getDeclaredFields();
        for (Field field:fields) {
            field.setAccessible(true);
            if (field.getName().equalsIgnoreCase(config.getConfigKey().replace("_",""))) {
                try {
                    if (config.getConfigVal()!=null) {
                        if (field.getType().equals(Integer.class)) {
                            field.set(this,Integer.valueOf(config.getConfigVal()));
                        }else if(field.getType().equals(Boolean.class)) {
                            String confVal = config.getConfigVal().trim();
                            if( confVal.equals("1")||confVal.equalsIgnoreCase("true")||confVal.equalsIgnoreCase("yes") ){
                                field.set(this,true);
                            }else {
                                field.set(this,false);
                            }
                        } else {
                            field.set(this,config.getConfigVal());
                        }
                    }
                }catch (IllegalAccessException e) {
                }
                break;
            }
        }
    }


    public void setExecUser(String execUser) {
        if (execUser!=null) {
            this.execUser = execUser.trim().replaceAll("\\s+,\\s+",",");
        }
    }

}