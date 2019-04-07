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
package com.jobxhub.server.controller;

import com.jobxhub.common.util.DateUtils;
import com.jobxhub.service.vo.RestResult;
import com.jobxhub.service.vo.RestStatus;
import lombok.extern.slf4j.Slf4j;
import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/verify")
@Slf4j
public class VerifyController {

    @PostMapping("recent")
    public RestResult recentExp(String cronExp) {
        List<String> list = new ArrayList<String>();
        try {
            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
            cronTriggerImpl.setCronExpression(cronExp);
            List<Date> dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null, 3);
            for (Date date : dates) {
                list.add(DateUtils.parseStringFromDate(date,DateUtils.format));
            }
            return RestResult.ok(list);
        } catch (ParseException e) {
            e.printStackTrace();
            return RestResult.rest(RestStatus.INVALID).setBody(e.getLocalizedMessage());
        }
    }

}
