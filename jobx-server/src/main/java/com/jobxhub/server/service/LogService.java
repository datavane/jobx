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
package com.jobxhub.server.service;

import com.google.common.collect.Lists;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.server.domain.LogBean;
import com.jobxhub.server.dao.LogDao;
import com.jobxhub.server.support.JobXTools;
import com.jobxhub.server.tag.PageBean;
import com.jobxhub.server.dto.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import java.util.List;

@Service
public class LogService {

    @Autowired
    private LogDao logDao;

    public void save(Log log) {
        LogBean logBean = LogBean.transfer.apply(log);
        logDao.save(logBean);
        log.setLogId(logBean.getLogId());
    }

    public void getByPageBean(HttpSession session, PageBean pageBean, Long agentId, String sendTime) {
        pageBean.put("agentId",agentId);
        pageBean.put("sendTime",sendTime);
        pageBean.put("userId",JobXTools.getUserId(session));
        List<LogBean> logBeans = logDao.getByPageBean(pageBean);
        if (CommonUtils.notEmpty(logBeans)) {
            int count = logDao.getCount(pageBean.getFilter());
            pageBean.setResult(Lists.transform(logBeans,Log.transfer));
            pageBean.setTotalRecord(count);
        }
    }

    public List<Log> getUnReadMessage(Long userId) {
        List<LogBean> beans = logDao.getUnRead(userId);
        return Lists.transform(beans,Log.transfer);
    }

    public Integer getUnReadCount(Long userId) {
        return logDao.getUnReadCount(userId);
    }

    public Log getById(Long logId) {
        LogBean logBean = logDao.getById(logId);
        if (logBean!=null) {
            return Log.transfer.apply(logBean);
        }
        return null;
    }

    public void updateAfterRead(Long logId) {
        logDao.updateRead(logId);
    }
}
