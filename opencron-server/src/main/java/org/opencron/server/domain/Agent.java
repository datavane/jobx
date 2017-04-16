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


package org.opencron.server.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencron.common.utils.CommonUtils;

import javax.persistence.*;

@Entity
@Table(name = "T_AGENT")
public class Agent implements Serializable {

    @Id
    @GeneratedValue
    private Long agentId;

    //代理执行器的Id
    private Long proxyAgent;

    private String ip;
    private Integer port;
    private String name;
    private String password;
    private Boolean warning;

    @Lob
    @Column(columnDefinition="TEXT")
    private String emailAddress;
    private String mobiles;
    private Boolean status;
    private Date failTime;
    private String comment;
    private Date updateTime;

    private Integer proxy;//是否需要代理

    /**
     * 新增一个得到task任务个数的字段，供页面显示使用
     */
    @Transient
    private Integer taskCount;

    @Transient
    private List<User> users = new ArrayList<User>();

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = CommonUtils.notEmpty(password) ? password.trim().toLowerCase() : "";
    }

    public Boolean getWarning() {
        return warning;
    }

    public void setWarning(Boolean warning) {
        this.warning = warning;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getMobiles() {
        return mobiles;
    }

    public void setMobiles(String mobiles) {
        this.mobiles = mobiles;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getFailTime() {
        return failTime;
    }

    public void setFailTime(Date failTime) {
        this.failTime = failTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getProxyAgent() {
        return proxyAgent;
    }

    public void setProxyAgent(Long proxyAgent) {
        this.proxyAgent = proxyAgent;
    }

    public Integer getProxy() {
        return proxy;
    }

    public void setProxy(Integer proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Agent agent = (Agent) o;

        return agentId.equals(agent.agentId);
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public int hashCode() {
        return agentId.hashCode();
    }

    @Override
    public String toString() {
        return "Agent{" +
                "agentId=" + agentId +
                ", proxyAgent=" + proxyAgent +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", warning=" + warning +
                ", emailAddress='" + emailAddress + '\'' +
                ", mobiles='" + mobiles + '\'' +
                ", status=" + status +
                ", failTime=" + failTime +
                ", comment='" + comment + '\'' +
                ", updateTime=" + updateTime +
                ", proxy=" + proxy +
                ", taskCount=" + taskCount +
                '}';
    }
}
