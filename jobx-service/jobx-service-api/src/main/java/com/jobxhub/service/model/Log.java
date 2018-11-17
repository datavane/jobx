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

import com.google.common.base.Function;
import com.jobxhub.service.entity.LogEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ChenHui on 2016/3/31.
 */
@Data
public class Log implements Serializable {

    private Long logId;

    private Long agentId;

    private Long userId;

    private Integer type;

    private String receiver;

    private String message;

    private String result;

    private Date sendTime;

    private String agentName;

    private Boolean isread;


    public Log() {

    }

    public static Function<LogEntity,Log> transferModel = new Function<LogEntity, Log>() {
        @Override
        public Log apply(LogEntity input) {
            Log log = new Log();
            BeanUtils.copyProperties(input,log);
            return log;
        }
    };

    public static Function<Log,LogEntity> transferEntity = new Function<Log,LogEntity>() {
        @Override
        public LogEntity apply(Log input) {
            LogEntity model = new LogEntity();
            BeanUtils.copyProperties(input,model);
            return model;
        }
    };

}