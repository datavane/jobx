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

import com.jobxhub.server.domain.AgentBean;
import com.jobxhub.server.tag.PageBean;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AgentDao {

    List<AgentBean> getAll();

    AgentBean getById(@Param("agentId") Long agentId);

    void save(AgentBean agent);

    void update(AgentBean agent);

    void delete(Long id);

    AgentBean getByMacId(@Param("macId") String macId);

    List<AgentBean> getByConnType(@Param("userId") Long userId, @Param("status") Integer status);

    List<AgentBean> getByPageBean(@Param("pager") PageBean pageBean);

    int getCount(@Param("filter") Map filter);

    int existsCount(@Param("id") Long id, @Param("key") String key, @Param("val") Object val);

    List<AgentBean> getByGroup(Long groupId);

    void updateStatus(@Param("agentId") Long agentId,@Param("status") Integer status);

    void updateNotifyTime(@Param("agentId") Long agentId,@Param("notifyTime")  Date date);
}
