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
package com.jobxhub.agent.service;

import com.alibaba.fastjson.JSON;
import com.jobxhub.agent.process.JobXProcess;
import com.jobxhub.common.Constants;
import com.jobxhub.common.Constants.ExitCode ;
import com.jobxhub.common.api.AgentJob;
import com.jobxhub.common.ext.ExtensionLoader;
import com.jobxhub.common.job.Action;
import com.jobxhub.common.job.Monitor;
import com.jobxhub.common.job.Request;
import com.jobxhub.common.job.Response;
import com.jobxhub.common.logging.LoggerFactory;
import com.jobxhub.common.util.*;
import com.jobxhub.common.util.collection.HashMap;
import com.jobxhub.registry.URL;
import com.jobxhub.registry.zookeeper.ZookeeperRegistry;
import com.jobxhub.registry.zookeeper.ZookeeperTransporter;
import com.jobxhub.rpc.Client;
import com.jobxhub.rpc.ServerHandler;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;

import java.io.File;
import java.util.*;

import static com.jobxhub.common.util.CommonUtils.*;


public class AgentService implements ServerHandler, AgentJob {

    private static Logger logger = LoggerFactory.getLogger(AgentService.class);

    private Client client = null;

    private MonitorService monitorService = new MonitorService();

    private Map<String,JobXProcess> processMap = new HashMap<String, JobXProcess>(0);

    private Map<String,String> lostResponse = new HashMap<String,String>(0);

    private static ZookeeperRegistry registry = null;

    public AgentService() {
        String registryAddress = SystemPropertyUtils.get(Constants.PARAM_JOBX_REGISTRY_KEY);
        URL url = URL.valueOf(registryAddress);
        ZookeeperTransporter transporter =  ExtensionLoader.load(ZookeeperTransporter.class);
        registry = new ZookeeperRegistry(url,transporter);
        File filePath = new File(Constants.JOBX_LOG_PATH);
        for (File file:filePath.listFiles()) {
            if (file.getName().matches("^\\.[a-z0-9]{32}\\.log$")) {
                String pid = file.getName().replaceAll("^\\.|\\.log$","");
                String log = IOUtils.readText(file, Constants.CHARSET_UTF8);
                if (CommonUtils.notEmpty(log)) {
                    lostResponse.put(pid,log);
                }
                file.delete();
            }
        }
    }

    @Override
    public Response handle(Request request) {
        Action action = request.getAction();
        if (Action.PATH.equals(action)) {
            return path(request);
        }
        //verify password...
        if (!SystemPropertyUtils.get(Constants.PARAM_JOBX_PASSWORD_KEY).equalsIgnoreCase(request.getPassword())) {
            Map<String,String> result = new HashMap<String, String>(0);
            result.put(Action.PING.name(),"0");
            return Response.response(request)
                    .setSuccess(false)
                    .setResult(result)
                    .setExitCode(ExitCode.ERROR_PASSWORD.getValue())
                    .setMessage(ExitCode.ERROR_PASSWORD.getDescription())
                    .end();
        }

        switch (action) {
            case PING:
                return ping(request);
            case PATH:
                return path(request);
            case LISTPATH:
                return listPath(request);
            case EXECUTE:
                return execute(request);
            case PASSWORD:
                return password(request);
            case KILL:
                return kill(request);
            case MACID:
                return macId(request);
            case PROXY:
                return proxy(request);
            case MONITOR:
                return monitor(request);
            case RESTART:
                restart(request);
                break;
        }
        return null;
    }

    /**
     * ping的时候将失联的信息返回server...
     * @param request
     * @return
     */
    @Override
    public Response ping(Request request) {
        Map<String, String> result = new HashMap<String, String>(0);
        //agent Platform...
        if (CommonUtils.isWindows()) {
            result.put(Constants.PARAM_OS_KEY, Constants.Platform.Windows.toString());
        } else {
            result.put(Constants.PARAM_OS_KEY, Constants.Platform.Unix.toString());
        }
        if (!lostResponse.isEmpty()) {
            result.putAll(lostResponse);
            lostResponse.clear();
        }
        return Response.response(request)
                .setResult(result)
                .setSuccess(true)
                .setExitCode(ExitCode.SUCCESS_EXIT.getValue())
                .end();
    }

    @Override
    public Response path(Request request) {
        //返回密码文件的路径...
        return Response.response(request).setSuccess(true)
                .setExitCode(ExitCode.SUCCESS_EXIT.getValue())
                .setMessage(Constants.JOBX_HOME)
                .end();
    }

    @Override
    public Response listPath(Request request) {
        Response response = Response.response(request).setExitCode(ExitCode.SUCCESS_EXIT.getValue());
        String path = request.getParams().getString(Constants.PARAM_LISTPATH_PATH_KEY);
        if (CommonUtils.isEmpty(path)) return response.setSuccess(false).end();
        File file = new File(path);
        if (!file.exists()) {
            return response.setSuccess(false).end();
        }
        Map<String, String> result = new HashMap<String, String>(0);
        List<Map<String, String>> data = new ArrayList<Map<String, String>>(0);
        for (File itemFile : file.listFiles()) {
            if (itemFile.isHidden()) continue;
            Map<String, String> itemMap = new HashMap<String, String>(0);
            itemMap.put(Constants.PARAM_LISTPATH_NAME_KEY, itemFile.getName());
            itemMap.put(Constants.PARAM_LISTPATH_PATH_KEY, itemFile.getAbsolutePath());
            itemMap.put(Constants.PARAM_LISTPATH_ISDIRECTORY_KEY, itemFile.isDirectory() ? "0" : "1");
            data.add(itemMap);
        }
        result.put(Constants.PARAM_LISTPATH_PATH_KEY, JSON.toJSONString(data));
        response.setSuccess(true).setResult(result).end();
        return response;
    }

    @Override
    public Response monitor(Request request) {
        Constants.ConnType connType = Constants.ConnType.getByName(request.getParams().getString("connType"));
        Response response = Response.response(request);
        switch (connType) {
            case PROXY:
                try {
                    Monitor monitor = monitorService.monitor();
                    Map<String, String> map = monitor.toMap();
                    response.setResult(map)
                            .setSuccess(true)
                            .setExitCode(ExitCode.SUCCESS_EXIT.getValue())
                            .end();
                    return response;
                } catch (SigarException e) {
                    e.printStackTrace();
                }
            default:
                return null;
        }
    }

    @Override
    public Response execute(final Request request) {

        String command = request.getParams().getString(Constants.PARAM_COMMAND_KEY);

        String pid = request.getParams().getString(Constants.PARAM_PID_KEY);

        Integer timeout = request.getTimeOut();

        String execUser = request.getParams().getString(Constants.PARAM_EXECUSER_KEY);

        if (logger.isInfoEnabled()) {
            logger.info("[JobX]:execute:{},pid:{}", command, pid);
        }

        Response response = Response.response(request);

        JobXProcess jobXProcess = new JobXProcess(command,timeout,pid,execUser);

        processMap.put(pid,jobXProcess);

        try {
            response.setExitCode(jobXProcess.start());
        }catch (Exception e) {
            response.setExitCode(-1);
        }finally {
            String message = jobXProcess.getLogMessage();
            response.setMessage(message);
            response.end();
            jobXProcess.deleteExecShell();
            //todo 得确保server和agent是连接的状态才可以清理log...
            jobXProcess.deleteLog();
            processMap.remove(pid);
        }
        return response;
    }

    @Override
    public Response password(Request request) {
        String newPassword = request.getParams().getString(Constants.PARAM_NEWPASSWORD_KEY);
        Response response = Response.response(request);
        if (isEmpty(newPassword)) {
            return response.setSuccess(false).setExitCode(ExitCode.SUCCESS_EXIT.getValue()).setMessage("密码不能为空").end();
        }

        //把老的注册删除
        unRegister(request.getHost(),request.getPort());

        SystemPropertyUtils.setProperty(Constants.PARAM_JOBX_PASSWORD_KEY, newPassword);
        IOUtils.writeText(Constants.JOBX_PASSWORD_FILE, newPassword, Constants.CHARSET_UTF8);

        //最新密码信息注册进来
        register(request.getHost(),request.getPort());

        return response.setSuccess(true).setExitCode(ExitCode.SUCCESS_EXIT.getValue()).end();
    }

    @Override
    public Response kill(Request request) {
        String pid = request.getParams().getString(Constants.PARAM_PID_KEY);
        if (logger.isInfoEnabled()) {
            logger.info("[JobX]:kill pid:{}", pid);
        }
        Response response = Response.response(request);
        JobXProcess jobXProcess = processMap.get(pid);
        if (jobXProcess!=null) {
            jobXProcess.kill(ExitCode.KILL);
            response.setExitCode(ExitCode.SUCCESS_EXIT.getValue()).end();
            if (logger.isInfoEnabled()) {
                logger.info("[JobX]:kill successful");
            }
        }else {
            response.setExitCode(ExitCode.ERROR_EXIT.getValue()).end();
            if (logger.isInfoEnabled()) {
                logger.info("[JobX]:kill error,can not found process");
            }
        }
        return response;
    }

    @Override
    public Response proxy(Request request) {
        if (this.client == null) {
            this.client = ExtensionLoader.load(Client.class);
        }
        String proxyHost = request.getParams().getString(Constants.PARAM_PROXYHOST_KEY);
        String proxyPort = request.getParams().getString(Constants.PARAM_PROXYPORT_KEY);
        String proxyAction = request.getParams().getString(Constants.PARAM_PROXYACTION_KEY);
        String proxyPassword = request.getParams().getString(Constants.PARAM_PROXYPASSWORD_KEY);
        //其他参数....
        String proxyParams = request.getParams().getString(Constants.PARAM_PROXYPARAMS_KEY);
        HashMap<String, Object> params = new HashMap<String, Object>(0);
        if (CommonUtils.notEmpty(proxyParams)) {
            params = (HashMap<String, Object>) JSON.parse(proxyParams);
        }

        Request proxyReq = Request.request(proxyHost, toInt(proxyPort), Action.findByName(proxyAction), proxyPassword, request.getTimeOut(), null).setParams(params);
        Response response;
        try {
            response = this.client.sentSync(proxyReq);
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.response(request);
            response.setExitCode(ExitCode.ERROR_EXIT.getValue())
                    .setMessage("[JobX]:proxy error:" + e.getLocalizedMessage())
                    .setSuccess(false)
                    .end();
        }
        return response;
    }

    @Override
    public Response macId(Request request) {
        String guid = getMacId();
        Response response = Response.response(request).end();
        if (notEmpty(guid)) {
            return response.setMessage(guid).setSuccess(true).setExitCode(ExitCode.SUCCESS_EXIT.getValue());
        }
        return response.setSuccess(false).setExitCode(ExitCode.ERROR_EXIT.getValue());
    }

    /**
     * 重启前先检查密码,密码不正确返回Response,密码正确则直接执行重启
     *
     * @param request
     * @return
     * @throws InterruptedException
     */
    @Override
    public void restart(Request request) {

    }

    public static void register(final String host,final Integer port) {
        /**
         * agent如果未设置host参数,则只往注册中心加入macId和password,server只能根据这个信息改过是否连接的状态
         * 如果设置了host,则会一并设置port,server端不但可以更新连接状态还可以实现agent自动注册(agent未注册的情况下)
         */
        registry.register(getRegistryPath(host,port), true);
        if (logger.isInfoEnabled()) {
            logger.info("[JobX] agent register to zookeeper done");
        }
    }

    public static void unRegister(final String host,final Integer port) {
        registry.unRegister(getRegistryPath(host,port));
        if (logger.isInfoEnabled()) {
            logger.info("[JobX] agent unRegister to zookeeper done");
        }
    }

    public static void bindShutdownHook(final String host,final Integer port) {
        //register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                if (logger.isInfoEnabled()) {
                    logger.info("[JobX] run shutdown hook now...");
                }
                registry.unRegister(getRegistryPath(host,port));
            }
        }, "JobXShutdownHook"));
    }

    private static String getRegistryPath(String host,Integer port) {
        //mac_password
        String machineId = getMacId();
        if (machineId == null) {
            throw new IllegalArgumentException("[JobX] getUniqueId error.");
        }

        String password = SystemPropertyUtils.get(Constants.PARAM_JOBX_PASSWORD_KEY);

        int platform = CommonUtils.getPlatform();
        //mac_password_platform_host_port
        String registryPath = String.format("%s/%s_%s_%s", Constants.ZK_REGISTRY_AGENT_PATH, machineId,password,platform);
        if (CommonUtils.isEmpty(host)) {
            if (logger.isWarnEnabled()) {
                logger.warn("[JobX] agent host not input,auto register can not be run，you can add this agent by yourself");
            }
        } else {
            //mac_password_platform_host_port
            registryPath = String.format("%s/%s_%s_%s_%s_%s",
                    Constants.ZK_REGISTRY_AGENT_PATH,
                    machineId,
                    password,
                    platform,
                    host,
                    port);
        }
        return registryPath;
    }

    /**
     * 从用户的home/.jobx下读取UID文件
     * @return
     */
    private static String getMacId() {
        String macId = null;
        if (Constants.JOBX_UID_FILE.exists()) {
            if (Constants.JOBX_UID_FILE.isDirectory()) {
                Constants.JOBX_UID_FILE.delete();
            } else {
                macId = IOUtils.readText(Constants.JOBX_UID_FILE, Constants.CHARSET_UTF8);
                if (CommonUtils.notEmpty(macId)) {
                    macId = StringUtils.clearLine(macId);
                    if (macId.length() != 32) {
                        Constants.JOBX_UID_FILE.delete();
                        macId = null;
                    }
                }
            }
        } else {
            Constants.JOBX_UID_FILE.getParentFile().mkdirs();
        }

        if (macId == null) {
            macId = MacUtils.getMachineId();
            IOUtils.writeText(Constants.JOBX_UID_FILE, macId, Constants.CHARSET_UTF8);
            Constants.JOBX_UID_FILE.setReadable(true,false);
            Constants.JOBX_UID_FILE.setWritable(false,false);
        }
        return macId;
    }


}