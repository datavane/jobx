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
package com.jobxhub.service;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import com.jobxhub.common.Constants;
import com.jobxhub.common.util.DateUtils;
import com.jobxhub.common.util.SystemPropertyUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Date;

@EnableTransactionManagement
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@MapperScan("com.jobxhub.service.dao")
@EnableDubbo
public class JobXServiceApplication {

    public static void main(String[] args) {

        SystemPropertyUtils.getOrElseUpdate(Constants.PARAM_JOBX_HOME_KEY, ".");

        //register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger logger = LoggerFactory.getLogger(JobXServiceApplication.class);
            logger.info("[JobX] run shutdown hook {}", DateUtils.formatFullDate(new Date()));
        }, "JobXServiceShutdownHook"));

        new SpringApplicationBuilder(JobXServiceApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

    }

}