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
package com.jobxhub.service.api;

import com.jobxhub.common.Constants;
import com.jobxhub.service.model.Chart;
import com.jobxhub.service.model.Record;
import com.jobxhub.service.vo.PageBean;

import java.util.List;

public interface RecordService {

    void getPageBean(Long userId,PageBean<Record> pageBean, Record record, boolean status);

    List<Record> getRedoList(Long recordId);

    Record getById(Long id);

    void save(Record record);

    Boolean isRunning(Long jobId);

    List<Chart> getReportChart(Long userId, String startTime, String endTime);

    Chart getTopChart(Long userId);

    Integer getRecordCount(Long userId, Constants.ResultStatus status, Constants.ExecType execType);

    void deleteRecord(String start, String end);
}
