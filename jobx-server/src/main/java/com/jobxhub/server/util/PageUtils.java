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

package com.jobxhub.server.util;

import com.jobxhub.common.util.DateUtils;
import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PageUtils {

    public static List<String> getRecentTriggerTime(String cron) {
        List<String> list = new ArrayList<String>();
        try {
            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
            cronTriggerImpl.setCronExpression(cron);
            List<Date> dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null, 5);
            for (Date date : dates) {
                list.add(DateUtils.parseStringFromDate(date,DateUtils.format));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }
}
