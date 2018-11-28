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

import com.jobxhub.common.Constants;
import com.jobxhub.common.exception.PingException;
import com.jobxhub.common.job.Action;
import com.jobxhub.common.job.Request;
import com.jobxhub.common.job.RequestFile;
import com.jobxhub.common.job.Response;
import com.jobxhub.rpc.InvokeCallback;
import com.jobxhub.server.util.Parser;
import com.jobxhub.server.job.JobXInvoker;
import com.jobxhub.server.dto.Agent;
import com.jobxhub.server.dto.Job;
import com.jobxhub.server.dto.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

import static com.jobxhub.common.Constants.*;

@Component
public class ExecuteService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RecordService recordService;

    @Autowired
    private JobService jobService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private JobXInvoker caller;

    @Autowired
    private AgentService agentService;

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));

    /**
     * 基本方式执行任务，按任务类型区分
     */
    public void executeJob(Job job, Constants.ExecType execType) {
        if (!checkJobPermission(job.getAgentId(), job.getUserId())) {
            return;
        }
        Agent agent = agentService.getAgent(job.getAgentId());
        job.setAgent(agent);
        JobType jobType = JobType.getJobType(job.getJobType());
        if (jobType.equals(JobType.SIMPLE)) {
            executeSimpleJob(job, execType);//单一任务
        }
        if (jobType.equals(JobType.FLOW)) {
            executeFlowJob(job, execType);//流程任务
        }
    }

    /**
     * 单一任务执行过程
     */
    private void executeSimpleJob(final Job job, final ExecType execType) {
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                final Record record = new Record(job, execType,JobType.SIMPLE);
                record.setCommand(Parser.parse(job));
                InvokeCallback invokeCallback = new ExecuteCallback(job,execType,record);
                try {
                    checkPing(job, record);
                    Agent agent = job.getAgent();
                    Request request = Request.request(agent.getHost(),
                            agent.getPort(),
                            Action.EXECUTE,
                            agent.getPassword(),
                            job.getTimeout(),
                            agent.getProxyId());
                    request.putParam(Constants.PARAM_COMMAND_KEY, record.getCommand());
                    request.putParam(Constants.PARAM_PID_KEY, record.getPid());
                    request.putParam(Constants.PARAM_SUCCESSEXIT_KEY, job.getSuccessExit());
                    request.putParam(Constants.PARAM_TIMEOUT_KEY, job.getTimeout());
                    request.putParam(Constants.PARAM_EXECUSER_KEY, job.getExecUser());
                    caller.sentAsync(request,invokeCallback);
                } catch (Exception e) {
                    if ( !(e instanceof PingException) ) {
                        invokeCallback.caught(e);
                    }
                }
            }
        });
    }

    /**
     * 流程任务 按流程任务处理方式区分
     */
    private void executeFlowJob(Job job, ExecType execType) {
        //todo....
    }

    /**
     * 多执行器同时 现场执行过程
     */
    public void executeBatchJob(final Long userId, String command, String agentIds) {
        String[] arrayIds = agentIds.split(";");
        final Semaphore semaphore = new Semaphore(arrayIds.length);
        ExecutorService exec = Executors.newCachedThreadPool();
        for (String agentId : arrayIds) {
            Agent agent = agentService.getAgent(Long.parseLong(agentId));
            final Job job = new Job(userId, command, agent);
            job.setSuccessExit(ExitCode.SUCCESS_EXIT.getValue().toString());
            exec.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        executeSimpleJob(job, ExecType.BATCH);
                        semaphore.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        exec.shutdown();
        while (true) {
            if (exec.isTerminated()) {
                if (logger.isInfoEnabled()) {
                    logger.info("[JobX]executeBatchJob done!");
                }
                break;
            }
        }
    }

    /**
     * 终止任务过程
     */
    public boolean killJob(final Record record) {
        if (record == null) return false;
        Job job = jobService.getById(record.getJobId());
        final Agent agent = agentService.getAgent(record.getAgentId());
        //现场执行的job
        if (record.getExecType().equals(ExecType.BATCH.getStatus())) {
            job = new Job(record.getUserId(),record.getCommand(),agent);
        }
        KillCallback killCallback = new KillCallback(job,record);
        try {
            Request request = Request.request(agent.getHost(),
                    agent.getPort(),
                    Action.KILL,
                    agent.getPassword(),
                    Constants.RPC_TIMEOUT,
                    agent.getProxyId());
            request.putParam(Constants.PARAM_PID_KEY, record.getPid());
            caller.sentAsync(request,killCallback);
            return true;
        } catch (Exception e) {
            if ( !(e instanceof PingException) ) {
                killCallback.caught(e);
                return false;
            }
        }
        return false;
    }

    private void responseToRecord(Response response, Record record) {
        record.setReturnCode(response.getExitCode());
        record.setMessage(response.getMessage());
        if (response.isSuccess()) {
            record.setSuccess(ResultStatus.SUCCESSFUL.getStatus());
        }else {
            record.setSuccess(ResultStatus.FAILED.getStatus());
        }
        int exitCode = response.getExitCode();
        if (exitCode == ExitCode.KILL.getValue()
                ||exitCode == ExitCode.OTHER_KILL.getValue()) {
            record.setStatus(RunStatus.STOPED.getStatus());
            record.setSuccess(ResultStatus.KILLED.getStatus());
        } else if (exitCode == ExitCode.TIME_OUT.getValue()) {
            record.setStatus(RunStatus.STOPED.getStatus());
            record.setSuccess(ResultStatus.TIMEOUT.getStatus());
        } else {
            record.setStatus(RunStatus.DONE.getStatus());
        }
        record.end();
    }

    public void lostToRecord(Record record) {
        record.setStatus(RunStatus.STOPED.getStatus());
        record.setSuccess(ResultStatus.LOST.getStatus());
        record.setEndTime(new Date());
        recordService.merge(record);
    }

    /**
     * 任务执行前 检测通信
     */
    private void checkPing(Job job, Record record) throws PingException {
        ConnStatus connStatus = ping(job.getAgent(),true);
        if (!connStatus.equals(ConnStatus.CONNECTED)) {
            //已完成
            record.setStatus(RunStatus.DONE.getStatus());
            record.setReturnCode(ExitCode.ERROR_PING.getValue());
            String format = "can't to communicate with agent:%s(%s:%d),execute job:%s failed";
            String content = String.format(format,
                    job.getAgent().getName(),
                    job.getAgent().getHost(),
                    job.getAgent().getPort(),
                    job.getJobName());
            record.setMessage(content);
            record.setSuccess(ResultStatus.LOST.getStatus());
            record.setEndTime(new Date());
            recordService.merge(record);
            throw new PingException(content);
        }
    }

    public Constants.ConnStatus ping(Agent agent, boolean update) {
        Response response = null;
        try {
            response = caller.sentSync(Request.request(
                    agent.getHost(),
                    agent.getPort(),
                    Action.PING,
                    agent.getPassword(),
                    Constants.RPC_TIMEOUT,
                    agent.getProxyId()));
        } catch (Exception e) {
            logger.error("[JobX]ping failed,host:{},port:{}", agent.getHost(), agent.getPort());
        }

        ConnStatus status = ConnStatus.DISCONNECTED;

        if (response != null) {
            if (response.isSuccess()) {
                status = ConnStatus.CONNECTED;
            }else {
                if (response.getResult().isEmpty()) {
                    status = ConnStatus.DISCONNECTED;
                }else {
                    status = ConnStatus.UNAUTHORIZED;
                }
            }

            //处理agent失联之后上报的log...
           /* if (!response.getResult().isEmpty()) {
                Map<String,String> result = response.getResult();
                for (Map.Entry<String,String> entry:result.entrySet()) {
                    if (entry.getKey().length() == 32) {
                        String log = entry.getValue();
                        if (CommonUtils.notEmpty(log)) {
                            String logInfo[] = log.split(IOUtils.FIELD_TERMINATED_BY);
                            String message = logInfo[0];
                            Integer exitCode = null;
                            Long entTime = null;
                            if (logInfo.length == 2) {
                                exitCode = Integer.parseInt(logInfo[1].split(IOUtils.TAB)[0]);
                                entTime = Long.parseLong(logInfo[1].split(IOUtils.TAB)[1]);
                            }
                            recordService.doLostLog(entry.getKey(),message,exitCode,entTime);
                        }
                    }
                }
            }*/
        }

        if (update) {
            agent.setStatus(status.getValue());
            agentService.saveOrUpdate(agent);
        }

        return status;
    }

    public String getMacId(Agent agent) {
        try {
            Response response = caller.sentSync(Request.request(
                    agent.getHost(),
                    agent.getPort(),
                    Action.MACID,
                    agent.getPassword(),
                    Constants.RPC_TIMEOUT,
                    agent.getProxyId())
            );
            return response.getMessage();
        } catch (Exception e) {
            logger.error("[JobX]getguid failed,host:{},port:{}", agent.getHost(), agent.getPort());
            return null;
        }
    }

    public String path(Agent agent) {
        try {
           return caller.sentSync(
                   Request.request(
                    agent.getHost(),
                    agent.getPort(),
                    Action.PATH,
                    null,
                    Constants.RPC_TIMEOUT,
                    agent.getProxyId())
           ).getMessage();
        } catch (Exception e) {
            logger.error("[JobX]ping failed,host:{},port:{}", agent.getHost(), agent.getPort());
            return null;
        }
    }

    public Response listPath(Agent agent, String path) {
        return caller.sentSync(Request.request(
                agent.getHost(),
                agent.getPort(),
                Action.LISTPATH,
                agent.getPassword(),
                Constants.RPC_TIMEOUT,
                agent.getProxyId()).putParam(Constants.PARAM_LISTPATH_PATH_KEY,path));
    }

    /**
     * 修改密码
     */
    public boolean password(Agent agent, final String newPassword) {
        boolean ping = false;
        try {
            Response response = caller.sentSync(Request.request(
                    agent.getHost(),
                    agent.getPort(),
                    Action.PASSWORD,
                    agent.getPassword(),
                    Constants.RPC_TIMEOUT,
                    agent.getProxyId()
                    ).putParam(
                    Constants.PARAM_NEWPASSWORD_KEY,
                    newPassword)
            );
            ping = response.isSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ping;
    }

    /**
     * 监测执行器运行状态
     */
    public Response monitor(Agent agent) throws Exception {
        return caller.sentSync(
                Request.request(
                        agent.getHost(),
                        agent.getPort(),
                        Action.MONITOR,
                        agent.getPassword(),
                        Constants.RPC_TIMEOUT,
                        agent.getProxyId()
                ));
    }


    public Response upload(Agent agent, RequestFile requestFile) {
        Request request = Request.request(
                agent.getHost(),
                agent.getPort(),
                Action.UPLOAD,
                agent.getPassword(),
                null,
                agent.getProxyId());

        request.setUploadFile(requestFile);
        Response response = caller.sentSync(request);
        if (!response.isSuccess()) {
            response.setSuccess(response.getExitCode() == ExitCode.SUCCESS_EXIT.getValue());
        }
        return response;
    }

    /**
     * 校验任务执行权限
     */
    private boolean checkJobPermission(Long jobAgentId, Long userId) {
       /* if (userId == null) {
            return false;
        }
        User user = userService.getUserById(userId);
        //超级管理员拥有所有执行器的权限
        if (user != null && user.getRoleId() == 999) {
            return true;
        }
        String agentIds = userService.getUserById(userId).getAgentIds();
        agentIds = "," + agentIds + ",";
        String thisAgentId = "," + jobAgentId + ",";
        return agentIds.contains(thisAgentId);*/
       return true;
    }

    private void printLog(String str, Job job, String message) {
        if (message != null) {
            if (logger.isInfoEnabled()) {
                logger.info(str, job.getJobName(), job.getAgent().getHost(), job.getAgent().getPort(), message);
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info(str, job.getJobName(), job.getAgent().getHost(), job.getAgent().getPort());
            }
        }
    }

    private String loggerError(String str, Job job, String message, Exception e) {
        String errorInfo = String.format(str, job.getJobName(), job.getAgent().getHost(), job.getAgent().getPort(), message);
        if (logger.isErrorEnabled()) {
            logger.error(errorInfo, e);
        }
        return errorInfo;
    }

    private void printLostJobInfo(Job job, String message) {

    }


    class ExecuteCallback implements InvokeCallback {
        private Record record;
        private ExecType execType;
        private Job job;

        public ExecuteCallback(Job job, ExecType execType, Record record) {
            this.job = job;
            this.record = record;
            this.execType = execType;
            //执行前先保存
            recordService.merge(record);
        }
        @Override
        public void done(Response response) {
            logger.info("[JobX]:execute response:{}", response.toString());
            try {
                responseToRecord(response,record);
                //api方式调度,回调结果数据给调用方
                job.callBack(response,execType);
                //防止返回的信息太大,往数据库存，有保存失败的情况发生
                recordService.merge(record);
                if (!response.isSuccess()) {
                    noticeService.notice(job, null);
                    printLog("execute failed:jobName:{} at host:{},port:{},info:{}", job, record.getMessage());
                } else {
                    printLog("execute successful:jobName:{} at host:{},port:{}", job, null);
                }

            } catch (Exception e) {
                //信息丢失,继续保存记录
                printLostJobInfo(job, record.getMessage());
                record.setMessage(null);
                recordService.merge(record);
                //发送警告信息
                noticeService.notice(job, e.getLocalizedMessage());
                loggerError("execute failed:jobName:%s at host:%s,port:%d,info:%s", job, e.getLocalizedMessage(), e);
            }
        }
        @Override
        public void caught(Throwable err) {
            //方法失联
            lostToRecord(record);
            noticeService.notice(job, "调用失败,获取不到返回结果集");
        }
    }

    class KillCallback implements InvokeCallback {
        private Record record;
        private Job job;
        public KillCallback(Job job,Record record) {
            this.job = job;
            this.record = record;
            record.setStatus(RunStatus.STOPPING.getStatus());
            recordService.merge(record);
        }

        @Override
        public void done(Response response) {
            record = recordService.getById(record.getRecordId());
            record.setStatus(RunStatus.STOPED.getStatus());
            record.setSuccess(ResultStatus.KILLED.getStatus());
            record.setEndTime(new Date());
            recordService.merge(record);
            printLog("killed successful :jobName:{} at host:{},port:{},pid:{}", job, record.getPid());
        }

        @Override
        public void caught(Throwable err) {
            printLog("killed error :jobName:{} at host:{},port:{},pid:{}", job, record.getPid());
        }
    }
}