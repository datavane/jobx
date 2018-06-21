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

package com.jobxhub.common.logging;


import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;

import java.io.File;
import java.net.URL;

/**
 * Created by benjobs on 14-4-28.
 */
public abstract class LoggerFactory {

    public static Logger getLogger(@SuppressWarnings("rawtypes") Class clazz) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("log4j.properties");
        String path;
        if (url == null) {
            String currPath = LoggerFactory.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            File file = new File(currPath);
            path = file.getParentFile().getParentFile() + "/conf/log4j.properties";
            if (!new File(path).exists()) {
                throw new ExceptionInInitializerError("[JobX] error: can not found log4j.properties...");
            }
        } else {
            path = url.getPath();
        }
        PropertyConfigurator.configure(path);
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }

}
