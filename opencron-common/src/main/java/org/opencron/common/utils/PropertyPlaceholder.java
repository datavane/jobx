/**
 * Copyright 2016 benjobs
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

package org.opencron.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Package: cn.damai.notify.util
 * @Description: TODO
 * @author: Wanghuajie
 * @date: 13-5-22 - 下午2:39
 * @version: V1.0
 * @company: damai
 */
public class PropertyPlaceholder extends PropertyPlaceholderConfigurer {

    private static Map<String,String> properties = new HashMap<String,String>();

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            properties.put(keyStr, value);
        }
    }

    public static String get(String name) {
        return properties.get(name);
    }

    public static Integer getInt(String name) {
        return CommonUtils.toInt(get(name));
    }

    public static Long getLong(String name) {
        return CommonUtils.toLong(get(name));
    }

    public static Float getFolat(String name) {
        return CommonUtils.toFloat(get(name));
    }

    public static Boolean getBoolean(String name) {
        String result = get(name);
        if (result==null) {
            return false;
        }
        return CommonUtils.toBoolean(result);
    }
    public static Map<String, String> getProperties() {
        return properties;
    }
}