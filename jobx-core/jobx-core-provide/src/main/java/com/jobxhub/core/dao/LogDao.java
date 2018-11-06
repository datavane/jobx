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
package com.jobxhub.core.dao;


import com.jobxhub.core.entity.LogEntity;
import com.jobxhub.core.vo.PageBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LogDao {

    LogEntity getById(@Param("logId") Long logId);

    List<LogEntity> getByPageBean(@Param("pager") PageBean page);

    int getCount(@Param("filter") Map filter);

    List<LogEntity> getUnRead(Long userId);

    Integer getUnReadCount(Long userId);

    void updateRead(Long logId);

    void save(LogEntity log);

}
