/**
 * Copyright 2016 benjobs
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


package org.opencron.server.vo;

import java.io.Serializable;

/**
 * Created by ChenHui on 2016/3/3.
 */
public class ChartVo implements Serializable {

    /**
     * 执行类型比例饼状图数据
     */
    private Integer auto;//自动执行

    private Integer operator;//手动执行

    private Integer rerun;//重执行

    /**
     * 执行成功失败比例图数据
     */
    private Integer success;//成功

    private Integer failure;//失败

    private Integer killed;//被杀

    private Integer singleton;//单一任务

    private Integer flow;//流程任务

    private Integer crontab;//crontab 类型

    private Integer quartz;//quartz 类型


    private String date;//折线图横坐标时间 格式 yy-MM-dd

    public Integer getAuto() {
        return auto;
    }

    public void setAuto(Integer auto) {
        this.auto = auto;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public Integer getRerun() {
        return rerun;
    }

    public void setRerun(Integer rerun) {
        this.rerun = rerun;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getFailure() {
        return failure;
    }

    public void setFailure(Integer failure) {
        this.failure = failure;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getKilled() {
        return killed;
    }

    public void setKilled(Integer killed) {
        this.killed = killed;
    }

    public Integer getSingleton() {
        return singleton;
    }

    public void setSingleton(Integer singleton) {
        this.singleton = singleton;
    }

    public Integer getFlow() {
        return flow;
    }

    public void setFlow(Integer flow) {
        this.flow = flow;
    }

    public Integer getCrontab() {
        return crontab;
    }

    public void setCrontab(Integer crontab) {
        this.crontab = crontab;
    }

    public Integer getQuartz() {
        return quartz;
    }

    public void setQuartz(Integer quartz) {
        this.quartz = quartz;
    }
}
