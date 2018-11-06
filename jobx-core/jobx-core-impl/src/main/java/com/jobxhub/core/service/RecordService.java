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


package com.jobxhub.core.service;

import com.google.common.collect.Lists;
import com.jobxhub.common.Constants;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.core.model.RecordModel;
import com.jobxhub.core.dao.RecordDao;
import com.jobxhub.core.model.RecordMessageModel;
import com.jobxhub.core.support.JobXTools;
import com.jobxhub.core.tag.PageBean;
import com.jobxhub.core.dto.Chart;
import com.jobxhub.core.dto.Record;
import com.jobxhub.core.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

import static com.jobxhub.common.util.CommonUtils.notEmpty;

@Service
public class RecordService {

    @Autowired
    private RecordDao recordDao;

    public void getPageBean(HttpSession session, PageBean<Record> pageBean,Record record,boolean status) {
        pageBean.put("record",record);
        pageBean.put("running",status);
        pageBean.put("currTime",new Date());
        if (!JobXTools.isPermission(session)) {
            User user = JobXTools.getUser(session);
            pageBean.put("userId",user.getUserId());
        }
        List<RecordModel> records = recordDao.getByPageBean(pageBean);
        if (CommonUtils.notEmpty(records)) {
            int count = recordDao.getCount(pageBean.getFilter());
            List<Record> recordList = new ArrayList<Record>(0);
            for (RecordModel bean:records) {
                Record item = Record.transferDto.apply(bean);
                List<Record> redoList = getRedoList(bean.getRecordId());
                if (CommonUtils.notEmpty(recordList)) {
                    item.setRedoList(redoList);
                    item.setRedoCount(redoList.size());
                }
                recordList.add(item);
            }
            pageBean.setResult(recordList);
            pageBean.setTotalRecord(count);
        }
    }

    private List<Record> getRedoList(Long recordId) {
        List<RecordModel> recordModels = recordDao.getRedoList(recordId);
        return Lists.transform(recordModels,Record.transferDto);
    }

    public Record getById(Long id) {
        RecordModel recordModel = recordDao.getById(id);
        Record record = Record.transferDto.apply(recordModel);
        RecordMessageModel messageModel = recordDao.getMessage(id);
        if (messageModel!=null) {
            record.setMessage(messageModel.getMessage());
        }
        return record;
    }

    public void merge(Record record) {
        RecordModel recordModel = Record.transferModel.apply(record);
        if (record.getRecordId() == null) {
            recordDao.save(recordModel);
            record.setRecordId(recordModel.getRecordId());
        } else {
            recordDao.update(recordModel);
        }

        //save message
        if (CommonUtils.notEmpty(record.getMessage())) {
            //TODO  new RecordMessageModel
            RecordMessageModel messageModel =  null;//new RecordMessageModel(null);
            messageModel.setRecordId(recordModel.getRecordId());
            recordDao.saveMessage(messageModel);
        }
    }

    /**
     * @param jobId
     * @return true:running false:noRun
     */
    public Boolean isRunning(Long jobId) {
        //找到当前任务所有执行过的流程任务
        int count = recordDao.getRunningCount(jobId);
        return count > 0;
    }

    public List<Chart> getReportChart(HttpSession session, String startTime, String endTime) {
        Map<String,Object> map = new HashMap<String, Object>(0);
        map.put("start",startTime);
        map.put("end",endTime);
        if (!JobXTools.isPermission(session)) {
            map.put("userId",JobXTools.getUserId(session));
        }
        return recordDao.getReportChart(map);
    }

    public Chart getTopChart(HttpSession session) {
        Map<String,Object> map = new HashMap<String, Object>(0);
        if (!JobXTools.isPermission(session)) {
            map.put("userId",JobXTools.getUserId(session));
        }
        return recordDao.getTopChart(map);
    }

    public List<Record> getRunningFlowJob(Long recordId) {
        return null;
    }

    public Integer getRecordCount(HttpSession session, Constants.ResultStatus status, Constants.ExecType execType) {
        Map<String, Object> filter = new HashMap<String, Object>(0);
        filter.put("success", status.getStatus());
        filter.put("execType", execType.getStatus());
        if (!JobXTools.isPermission(session)) {
            filter.put("userId",JobXTools.getUserId(session));
        }
        filter.put("status", Arrays.asList(Constants.RunStatus.STOPED.getStatus(), Constants.RunStatus.DONE.getStatus(), Constants.RunStatus.RERUNDONE.getStatus()));
        return recordDao.getRecordCount(filter);
    }

    public void deleteRecord(String startTime, String endTime) {
        if (notEmpty(startTime, endTime)) {
            startTime = startTime.trim();
            endTime = endTime.trim();
            recordDao.delete(startTime,endTime);
            recordDao.deleteMessage(startTime,endTime);
        }
    }

    public Record getReRunningSubJob(Long recordId) {
       //todo
        return null;
    }

    public void doLostLog(String pid, String message, Integer exitCode, Long entTime) {
        RecordModel recordModel =  recordDao.getByPid(pid);
        if (recordModel!=null) {
            recordModel.setEndTime(new Date(entTime));
            recordModel.setReturnCode(exitCode);
            if (exitCode == Constants.ExitCode.SUCCESS_EXIT.getValue()) {
                recordModel.setSuccess(Constants.ResultStatus.SUCCESSFUL.getStatus());
            }else {
                recordModel.setSuccess(Constants.ResultStatus.FAILED.getStatus());

            }
            if (exitCode == Constants.ExitCode.KILL.getValue()) {
                recordModel.setStatus(Constants.RunStatus.STOPED.getStatus());
                recordModel.setSuccess(Constants.ResultStatus.KILLED.getStatus());
            } else if (exitCode == Constants.ExitCode.TIME_OUT.getValue()) {
                recordModel.setStatus(Constants.RunStatus.STOPED.getStatus());
                recordModel.setSuccess(Constants.ResultStatus.TIMEOUT.getStatus());
            } else {
                recordModel.setStatus(Constants.RunStatus.DONE.getStatus());
            }
            recordDao.update(recordModel);
            RecordMessageModel messageModel = new RecordMessageModel();
            messageModel.setRecordId(recordModel.getRecordId());
            messageModel.setMessage(message);
            messageModel.setStartTime(recordModel.getStartTime());
            recordDao.saveMessage(messageModel);
        }

    }
}
