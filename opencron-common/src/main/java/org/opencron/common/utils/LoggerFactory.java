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


import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;

import java.io.File;

/**
 * Created by benjobs on 14-4-28.
 */
public abstract class LoggerFactory {

    public static Logger getLogger(@SuppressWarnings("rawtypes") Class clazz) {
        String currPath = LoggerFactory.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        File file = new File(currPath);
        String path = file.getParentFile().getParentFile() + "/conf/log4j.properties";
        if (!new File(path).exists()) {
            throw new ExceptionInInitializerError("[opencron] error: can not found log4j.properties...");
        }
        PropertyConfigurator.configure(path);
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }

}
