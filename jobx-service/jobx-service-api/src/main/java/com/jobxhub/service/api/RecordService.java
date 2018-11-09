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
