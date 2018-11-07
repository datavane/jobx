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
package com.jobxhub.service.dao;

import com.jobxhub.service.entity.TerminalEntity;
import com.jobxhub.service.vo.PageBean;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TerminalDao {

    List<TerminalEntity> getByPageBean(@Param("pager") PageBean page);

    List<TerminalEntity> getByUser(Long userId);

    TerminalEntity getById(Long id);

    int getCount(@Param("filter") Map<String, Object> filter);

    void save(TerminalEntity terminal);

    void update(TerminalEntity terminal);

    void updateLoginTime(@Param("id")Long id,@Param("loginTime")Date date);

    void updateTheme(@Param("id") Long id,@Param("theme") String theme);

    void delete(Long id);

}
