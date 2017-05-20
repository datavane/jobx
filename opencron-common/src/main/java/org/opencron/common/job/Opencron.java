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
 * <p>
 */

package org.opencron.common.job;

import javax.annotation.Generated;
import java.io.Serializable;

/**
 * @author <a href="mailto:benjobs@qq.com">benjobs@qq.com</a>
 * @version 1.0.0
 * @name Opencron
 * @company org.opencron
 * @date 2017-05-20 pa 18:03<br/><br/>
 * <p>
 * <hr style="color:RED"/>
 * 我能抽象出整个世界<br/>
 * 但是我不能抽象出你<br/>
 * 你在我心中是那么的具体<br/>
 * 千万行代码怎么将你描绘<br/>
 * 我可以重载覆盖这个世界里的任何方法<br/>
 * 但却不能重载对你的思念<br/>
 * 也许是命中注定<br/>
 * 你在我的世界里永远的烙上了静态的属性<br/>
 * 我不慎调用了爱你这个方法<br/>
 * 当我义无返顾的把自己作为参数传进这个方法时<br/>
 * 我才发现爱上你是一个死循环<br/>
 * 它不停的返回对你的思念<br/>
 * 压入我心里的堆栈<br/>
 * 在这无尽的黑夜中<br/>
 * 我的内存里已经再也装不下别人<br/>
 * <hr style="color:RED"/>
 */

@Generated(value = "created by @benjobs", date = "2017-05-20")
public interface Opencron {

    void ping(Request request) throws Exception;

    void path(Request request) throws Exception;

    void monitor(Request request) throws Exception;

    void execute(Request request) throws Exception;

    void password(Request request) throws Exception;

    void kill(Request request)throws Exception;

    void proxy(Request request)throws Exception;

    void guid(Request request)throws Exception;

    void restart(Request request) throws Exception;

    public enum StatusCode implements Serializable {
        SUCCESS_EXIT(0x0, "正常退出"),
        ERROR_EXIT(0x1, "异常退出"),
        ERROR_PING(-0x63, "连接失败,ping不通"),
        KILL(0x89, "进程被kill"),
        NOTFOUND(0x7f, "未找到命令或文件"),
        ERROR_EXEC(-0x64, "连接成功，执行任务失败!"),
        ERROR_PASSWORD(-0x1f4, "密码不正确!"),
        TIME_OUT(0x1f8, "任务超时");

        private Integer value;
        private String description;

        StatusCode(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public enum ExecType implements Serializable {

        AUTO(0x0, "auto", "自动模式,系统调用"),
        OPERATOR(0x1, "operator", "手动模式,手动调用"),
        RERUN(0x2, "rerun", "重跑模式"),
        BATCH(0x3, "batch", "现场执行");

        private Integer status;
        private String name;
        private String description;

        ExecType(Integer status, String name, String description) {
            this.status = status;
            this.name = name;
            this.description = description;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public static ExecType getByStatus(Integer status) {
            for (ExecType execType : ExecType.values()) {
                if (execType.getStatus().equals(status)) {
                    return execType;
                }
            }
            return null;
        }
    }

    public enum CronType implements Serializable {

        CRONTAB(0x0, "crontab", "crontab表达式"),
        QUARTZ(0x1, "quartz", "quartz表达式");

        private Integer type;
        private String name;
        private String description;

        CronType(Integer type, String name, String description) {
            this.type = type;
            this.name = name;
            this.description = description;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public static CronType getByType(Integer type) {
            for (CronType cronType : CronType.values()) {
                if (cronType.getType().equals(type)) {
                    return cronType;
                }
            }
            return null;
        }
    }

    public enum ResultStatus {
        FAILED(0x0, "失败"),
        SUCCESSFUL(0x1, "成功"),
        KILLED(0x2, "被杀"),
        TIMEOUT(0x3, "超时");

        private Integer status;
        private String description;

        ResultStatus(Integer status, String description) {
            this.status = status;
            this.description = description;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public enum RunStatus implements Serializable {
        RUNNING(0x0, "running", "正在运行"),
        DONE(0x1, "done", "已完成"),
        STOPPING(0x2, "stopping", "正在停止"),
        STOPED(0x3, "stoped", "已停止"),
        RERUNNING(0x4, "rerunning", "正在重跑"),
        RERUNUNDONE(0x5, "rerunundone", "重跑未完成"),
        RERUNDONE(0x6, "rerundone", "重跑完成");

        private Integer status;
        private String name;
        private String description;

        RunStatus(Integer status, String name, String description) {
            this.status = status;
            this.name = name;
            this.description = description;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public enum JobType implements Serializable {
        SINGLETON(0x0, "单一任务"),
        FLOW(0x1, "流程任务");

        private Integer code;
        private String description;

        JobType(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public static JobType getJobType(Integer type) {
            if (type == null) return null;
            for (JobType jobType : JobType.values()) {
                if (jobType.getCode().equals(type)) {
                    return jobType;
                }
            }
            return null;
        }
    }

    public enum MsgType {
        EMAIL(0x0, "邮件"),
        SMS(0x1, "短信"),
        WEBSITE(0x2, "站内信");

        private Integer value;
        private String desc;

        MsgType(Integer value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    public enum RunModel {
        SEQUENCE(0x0, "串行"),
        SAMETIME(0x1, "并行");
        private Integer value;
        private String desc;

        RunModel(Integer value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public static RunModel getRunModel(Integer value) {
            for (RunModel model : RunModel.values()) {
                if (model.getValue().equals(value)) {
                    return model;
                }
            }
            return null;
        }
    }

    public enum ConnType {
        CONN(0x0, "conn", "直连"),
        PROXY(0x1, "proxy", "代理");

        private Integer type;
        private String name;
        private String desc;

        ConnType(Integer type, String name, String desc) {
            this.type = type;
            this.name = name;
            this.desc = desc;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public static ConnType getByType(Integer type) {
            for (ConnType connType : ConnType.values()) {
                if (connType.getType().equals(type)) {
                    return connType;
                }
            }
            return null;
        }

        public static ConnType getByName(String name) {
            for (ConnType connType : ConnType.values()) {
                if (connType.getName().equals(name)) {
                    return connType;
                }
            }
            return null;
        }

    }

}