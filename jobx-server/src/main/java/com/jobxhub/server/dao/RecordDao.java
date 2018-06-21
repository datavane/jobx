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
package com.jobxhub.server.dao;

import com.jobxhub.server.domain.RecordBean;
import com.jobxhub.server.domain.RecordMessageBean;
import com.jobxhub.server.tag.PageBean;
import com.jobxhub.server.dto.Chart;
import com.jobxhub.server.dto.Record;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RecordDao {

    List<RecordBean> getByPageBean(@Param("pager") PageBean<Record> pageBean);

    int getCount(@Param("filter") Map<String, Object> filter);

    RecordBean getById(Long id);

    RecordBean getByPid(String pid);

    void save(RecordBean record);

    void update(RecordBean record);

    Chart getTopChart(@Param("filter") Map<String, Object> filter);

    List<Chart> getReportChart(@Param("filter") Map<String, Object> filter);

    int getRunningCount(@Param("jobId") Long jobId);

    int getRecordCount(@Param("filter") Map<String, Object> filter);

    void delete(@Param("start")String startTime,@Param("end") String endTime);

    List<RecordBean> getRedoList(Long recordId);

    RecordMessageBean getMessage(@Param("recordId") Long id);

    void deleteMessage(@Param("start")String startTime,@Param("end") String endTime);

    void saveMessage(RecordMessageBean messageBean);

}
