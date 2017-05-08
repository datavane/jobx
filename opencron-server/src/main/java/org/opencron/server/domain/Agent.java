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

    //执行器机器的唯一id(当前取的是机器的MAC地址)
    private String machineId;

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
    private Boolean deleted;//是否删除
    private Date failTime;
    private String comment;
    private Date updateTime;

    private Integer proxy;//是否需要代理

    private Long groupId;//对应的agentGroup

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

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Agent agent = (Agent) o;

        if (getAgentId() != null ? !getAgentId().equals(agent.getAgentId()) : agent.getAgentId() != null) return false;
        if (getMachineId() != null ? !getMachineId().equals(agent.getMachineId()) : agent.getMachineId() != null)
            return false;
        if (getProxyAgent() != null ? !getProxyAgent().equals(agent.getProxyAgent()) : agent.getProxyAgent() != null)
            return false;
        if (getIp() != null ? !getIp().equals(agent.getIp()) : agent.getIp() != null) return false;
        if (getPort() != null ? !getPort().equals(agent.getPort()) : agent.getPort() != null) return false;
        if (getName() != null ? !getName().equals(agent.getName()) : agent.getName() != null) return false;
        if (getPassword() != null ? !getPassword().equals(agent.getPassword()) : agent.getPassword() != null)
            return false;
        if (getWarning() != null ? !getWarning().equals(agent.getWarning()) : agent.getWarning() != null) return false;
        if (getEmailAddress() != null ? !getEmailAddress().equals(agent.getEmailAddress()) : agent.getEmailAddress() != null)
            return false;
        if (getMobiles() != null ? !getMobiles().equals(agent.getMobiles()) : agent.getMobiles() != null) return false;
        if (getStatus() != null ? !getStatus().equals(agent.getStatus()) : agent.getStatus() != null) return false;
        if (getDeleted() != null ? !getDeleted().equals(agent.getDeleted()) : agent.getDeleted() != null) return false;
        if (getFailTime() != null ? !getFailTime().equals(agent.getFailTime()) : agent.getFailTime() != null)
            return false;
        if (getComment() != null ? !getComment().equals(agent.getComment()) : agent.getComment() != null) return false;
        if (getUpdateTime() != null ? !getUpdateTime().equals(agent.getUpdateTime()) : agent.getUpdateTime() != null)
            return false;
        if (getProxy() != null ? !getProxy().equals(agent.getProxy()) : agent.getProxy() != null) return false;
        if (getGroupId() != null ? !getGroupId().equals(agent.getGroupId()) : agent.getGroupId() != null) return false;
        if (getTaskCount() != null ? !getTaskCount().equals(agent.getTaskCount()) : agent.getTaskCount() != null)
            return false;
        return getUsers() != null ? getUsers().equals(agent.getUsers()) : agent.getUsers() == null;
    }

    @Override
    public int hashCode() {
        int result = getAgentId() != null ? getAgentId().hashCode() : 0;
        result = 31 * result + (getMachineId() != null ? getMachineId().hashCode() : 0);
        result = 31 * result + (getProxyAgent() != null ? getProxyAgent().hashCode() : 0);
        result = 31 * result + (getIp() != null ? getIp().hashCode() : 0);
        result = 31 * result + (getPort() != null ? getPort().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getWarning() != null ? getWarning().hashCode() : 0);
        result = 31 * result + (getEmailAddress() != null ? getEmailAddress().hashCode() : 0);
        result = 31 * result + (getMobiles() != null ? getMobiles().hashCode() : 0);
        result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
        result = 31 * result + (getDeleted() != null ? getDeleted().hashCode() : 0);
        result = 31 * result + (getFailTime() != null ? getFailTime().hashCode() : 0);
        result = 31 * result + (getComment() != null ? getComment().hashCode() : 0);
        result = 31 * result + (getUpdateTime() != null ? getUpdateTime().hashCode() : 0);
        result = 31 * result + (getProxy() != null ? getProxy().hashCode() : 0);
        result = 31 * result + (getGroupId() != null ? getGroupId().hashCode() : 0);
        result = 31 * result + (getTaskCount() != null ? getTaskCount().hashCode() : 0);
        result = 31 * result + (getUsers() != null ? getUsers().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "agentId=" + agentId +
                ", machineId='" + machineId + '\'' +
                ", proxyAgent=" + proxyAgent +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", warning=" + warning +
                ", emailAddress='" + emailAddress + '\'' +
                ", mobiles='" + mobiles + '\'' +
                ", status=" + status +
                ", deleted=" + deleted +
                ", failTime=" + failTime +
                ", comment='" + comment + '\'' +
                ", updateTime=" + updateTime +
                ", proxy=" + proxy +
                ", groupId=" + groupId +
                ", taskCount=" + taskCount +
                ", users=" + users +
                '}';
    }
}
