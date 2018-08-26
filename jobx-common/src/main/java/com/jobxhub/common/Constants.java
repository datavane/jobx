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
package com.jobxhub.common;

import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.EnumUtil;
import com.jobxhub.common.util.PropertyPlaceholder;
import com.jobxhub.common.util.SystemPropertyUtils;

import java.io.File;
import java.io.Serializable;
import java.util.regex.Pattern;

public class Constants {

    public static final String JOBX_VERSION = "V1.2.0";

    public static final int ZK_CONNECTION_TIMEOUT = 2000;

    public static final int RPC_TIMEOUT = 5000;

    public static final String META_INF_DIR = "META-INF/jobx/";

    public static final String ZK_REGISTRY_AGENT_PATH = "/jobx/agent";

    public static final String ZK_REGISTRY_SERVER_PATH = "/jobx/server";

    public static final String ZK_REGISTRY_JOB_PATH = "/jobx/job";

    public static final String ZK_REGISTRY_TERM_PATH = "/jobx/term";

    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

    public static final String SESSION_CONF_FORMAT = "app-session-%s.xml";

    public static final int HEADER_SIZE = 4;

    public static final int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    public static final String DEFAULT_KEY_PREFIX = "default.";

    //============================== param ==============================//


    public static final String PARAM_JAVA_LIBRARY_PATH_KEY = "java.library.path";

    public static final String PARAM_COOKIE_NAME_KEY = "JOBX_UID";

    public static final String PARAM_DEF_USER_KEY = "jobx";

    public static final String PARAM_DEF_PASSWORD_KEY = "jobx";

    public static final String PARAM_PROXYHOST_KEY = "proxyHost";

    public static final String PARAM_PROXYPORT_KEY = "proxyPort";

    public static final String PARAM_PROXYACTION_KEY = "proxyAction";

    public static final String PARAM_PROXYPASSWORD_KEY = "proxyPassword";

    public static final String PARAM_PROXYPARAMS_KEY = "proxyParams";

    public static final String PARAM_MONITORPORT_KEY = "jobx.monitorPort";

    public static final String PARAM_NEWPASSWORD_KEY = "newPassword";

    public static final String PARAM_OS_KEY = "os";

    public static final String PARAM_PID_KEY = "pid";

    public static final String PARAM_COMMAND_KEY = "command";

    public static final String PARAM_TIMEOUT_KEY = "timeout";

    public static final String PARAM_EXECUSER_KEY = "execUser";

    public static final String PARAM_BACKUP_KEY = "backup";

    public static final String PARAM_SUCCESSEXIT_KEY = "successExit";

    public static final String PARAM_CACHED_NAME_KEY = "jobx.cached";

    public static final String PARAM_JOBX_CLUSTER_KEY = "jobx.cluster";

    public static final String PARAM_JOBX_KEYPATH_KEY = "jobx.keypath";

    public static final String PARAM_JOBX_PASSWORD_KEY = "jobx.password";

    public static final String PARAM_JOBX_HOST_KEY = "jobx.host";

    public static final String PARAM_JOBX_SHUTDOWN_KEY = "jobx.shutdown";

    public static final String PARAM_JOBX_REGISTRY_KEY = "jobx.registry";

    public static final String PARAM_JOBX_HOME_KEY = "jobx.home";

    public static final String PARAM_JOBX_PORT_KEY = "jobx.port";

    public static final String PARAM_CACHED_AGENT_KEY = "jobx_agent";

    public static final String PARAM_CACHED_JOB_KEY = "jobx_job";

    public static final String PARAM_LOGIN_USER_KEY = "jobx_user";

    public static final String PARAM_LOGIN_USER_ID_KEY = "jobx_user_id";

    public static final String PARAM_PERMISSION_KEY = "permission";

    public static final String PARAM_HTTP_SESSION_ID_KEY = "http_session_id";

    public static final String PARAM_TERMINAL_TOKEN_KEY = "jobx_term_token";

    public static final String PARAM_TERMINAL_PREFIX_KEY = "jobx_term_";

    public static final String PARAM_LISTPATH_PATH_KEY = "path";

    public static final String PARAM_LISTPATH_NAME_KEY = "name";

    public static final String PARAM_LISTPATH_ISDIRECTORY_KEY = "isDirectory";

    public static final String PARAM_CSRF_NAME_KEY = "csrf";

    public static final String PARAM_LOGIN_MSG_KEY = "loginMsg";

    public static final String PARAM_CONTEXT_PATH_NAME_KEY = "contextPath";

    public static final String PARAM_SKIN_NAME_KEY = "skin";

    public static final String PARAM_ANYHOST_KEY = "anyhost";

    public static final String PARAM_ANYHOST_VALUE = "0.0.0.0";

    public static final String CHARSET_GBK = "GBK";

    public static final String CHARSET_UTF8 = "UTF-8";

    public static final int WEB_THREADPOOL_SIZE = 500;

    //============================== param end ==============================//
    /**
     * Name of the system property containing
     */

    public static final Boolean JOBX_CLUSTER = PropertyPlaceholder.getBoolean(Constants.PARAM_JOBX_CLUSTER_KEY);

    public static final String JOBX_CACHED = PropertyPlaceholder.get(Constants.PARAM_CACHED_NAME_KEY);

    public static final String JOBX_HOME = SystemPropertyUtils.get(PARAM_JOBX_HOME_KEY,"");

    public static final String JOBX_NATIVE_PATH = JOBX_HOME + "/native";

    public static final String JOBX_LOG_PATH = JOBX_HOME + "/logs";

    public static final String JOBX_TMP_PATH = JOBX_HOME + "/temp";

    public static final String JOBX_USER_HOME = SystemPropertyUtils.get("user.home") + File.separator + ".jobx";

    public static final File JOBX_UID_FILE = new File(JOBX_USER_HOME,"id");

    /**
     * password file
     */

    public static final File JOBX_PASSWORD_FILE = new File(JOBX_HOME + File.separator + ".password");

    /**
     * pid
     */
    public static final File JOBX_PID_FILE = new File(SystemPropertyUtils.get("jobx.pid", JOBX_HOME + "/jobx.pid"));

    /**
     * monitor file
     */
    public static final File JOBX_MONITOR_SHELL = new File(JOBX_HOME + "/bin/monitor.sh");

    /**
     * ExecuteUser lib
     */
    public static final String JOBX_EXECUTE_AS_USER_LIB =  JOBX_HOME + "/bin/executor.so";


    public enum CachedProvider implements Serializable {

        REDIS("redis"),
        MEMCACHED("memcached");
        private String name;

        CachedProvider(String name) {
            this.name = name;
        }

        public static CachedProvider getByName(String name) {
            if (CommonUtils.isEmpty(name)) {
                return null;
            }
            for (CachedProvider provider : CachedProvider.values()) {
                if (provider.getName().equalsIgnoreCase(name)) {
                    return provider;
                }
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public enum ExitCode implements Serializable {
        SUCCESS_EXIT(0x0, "正常退出"),
        ERROR_EXIT(0x1, "异常退出"),
        ERROR_PING(-0x63, "连接失败,ping不通"),
        KILL(0x8f, "进程被kill"),
        OTHER_KILL(0x89, "进程被外部kill"),
        NOTFOUND(0x7f, "未找到命令或文件"),
        ERROR_EXEC(-0x64, "连接成功，执行任务失败!"),
        ERROR_PASSWORD(-0x1f4, "密码不正确!"),
        TIME_OUT(0x1f8, "任务超时");

        private Integer value;
        private String description;

        ExitCode(Integer value, String description) {
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
        API(0x2, "api", "api模式,通过接口调用"),
        RERUN(0x3, "rerun", "重跑模式"),
        BATCH(0x4, "batch", "现场执行");

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

    public enum ConnStatus implements Serializable {
        CONNECTED(0x1,  "通信成功"),
        DISCONNECTED(0x0, "通信失败"),
        UNAUTHORIZED(0x2, "密码错误,认证失败");

        private Integer value;
        private String description;

        ConnStatus(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public static ConnStatus getByValue(Integer value) {
            for (ConnStatus connStatus : ConnStatus.values()) {
                if (connStatus.getValue().equals(value)) {
                    return connStatus;
                }
            }
            return null;
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

    public enum ResultStatus implements EnumUtil.CommonEnum {
        FAILED(0x0, "失败"),
        SUCCESSFUL(0x1, "成功"),
        KILLED(0x2, "被杀"),
        TIMEOUT(0x3, "超时"),
        LOST(0x4, "方法调用失联");

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


        @Override
        public int getCode() {
            return status;
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
        SIMPLE(0x0, "简单作业"),
        FLOW(0x1, "工作流");

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
            if (type == null) {
                return null;
            }
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

    public enum CreateType {
        NORMAL(1, "正常定义的"),
        FLOW(2, "创建工作流时定义的");
        private Integer value;
        private String desc;

        CreateType(Integer value, String desc) {
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

        public static CreateType getCreateType(Integer value) {
            for (CreateType type : CreateType.values()) {
                if (type.getValue().equals(value)) {
                    return type;
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


    public enum SshType {
        ACCOUNT(0x0),
        SSHKEY(0x1);

        private Integer type;

        SshType(Integer type) {
            this.type = type;
        }

        public static SshType getByType(Integer type) {
            for (SshType sshType : SshType.values()) {
                if (sshType.getType().equals(type)) {
                    return sshType;
                }
            }
            return null;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }
    }

    public enum LauncherType {
        TOMCAT("tomcat"),
        JETTY("jetty");

        private String name;

        LauncherType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static ConnType getByName(String name) {
            for (ConnType connType : ConnType.values()) {
                if (connType.getName().equals(name)) {
                    return connType;
                }
            }
            return null;
        }

        public static boolean isTomcat(String name) {
            if (CommonUtils.isEmpty(name)) {
                return false;
            }
            return TOMCAT.getName().equalsIgnoreCase(name.trim());
        }

        public static boolean isJetty(String name) {
            if (CommonUtils.isEmpty(name)) {
                return false;
            }
            return JETTY.getName().equalsIgnoreCase(name.trim());
        }
    }

    public enum Platform {

        Windows("Windows",0),

        Unix("Unix",1),

        Linux("Linux",2),

        Mac_OS("Mac OS",3),

        Mac_OS_X("Mac OS X",4),

        OS2("OS/2",5),

        Solaris("Solaris",6),

        SunOS("SunOS",7),

        MPEiX("MPE/iX",8),

        HP_UX("HP-UX",9),

        AIX("AIX",10),

        OS390("OS/390",11),

        FreeBSD("FreeBSD",12),

        Irix("Irix",13),

        Digital_Unix("Digital Unix",14),

        NetWare_411("NetWare",15),

        OSF1("OSF1",16),

        OpenVMS("OpenVMS",17),

        Others("Others",18);

        private String name;
        private int index;

        Platform(String name,int index){
            this.name = name;
            this.index = index;
        }

        public String toString(){
            return name;
        }

        public static Platform getByName(String name) {
            for (Platform platform : Platform.values()) {
                if (platform.getName().equals(name)) {
                    return platform;
                }
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }




}
