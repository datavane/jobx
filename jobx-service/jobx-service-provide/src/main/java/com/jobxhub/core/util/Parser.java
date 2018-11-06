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

package com.jobxhub.core.util;
/**
 * @Package com.jobxhub.server.common
 * @Title: Parser
 * @author hitechr
 * @date 2018/6/8 21:02
 * @version V1.0
 */

import com.jobxhub.common.util.DateUtils;
import com.jobxhub.common.util.StringUtils;
import com.jobxhub.core.dto.Job;

import java.util.Date;

/**
 * @Descriptions: 参数替换工具类
 * $date: 当期日期 2017-08-01
 * $time: 当前时间 20180609210423
 * $user: 作业的用户
 * $jobId: 作业的ID
 * $pid: 作业记录对应的pid
 * $name: 作业的名称
 *
 */
public class Parser {

    public  final static String MARK="$";

    /**
     * a.sh 1 2 $date 3 $name 5 $jobId $pid
     * inputParam=2017-3-4 Lily 21 1111111
     * @param job
     * @return
     */
    public static String parse(Job job){
        String inputParam = job.getInputParam();
        //按默认替换
        if(StringUtils.isBlank(inputParam)){//如果没有传递参数，则默认取值
            String cmdValue=parseDefault(job);
            return cmdValue.trim();
        }
        //append
        String command = job.getCommand();
        int f$ = command.indexOf(MARK);
        if(f$<0){// a.sh 1 2 3
            return command.trim()+" "+inputParam.trim();
        }
        return parse2(job);
        // a.sh 1 2 $date 3 $name 5 $jobId $pid
    }

    /**
     * a.sh 1 2 $date 3 $name 5 $jobId $pid
     *  inputParam=2017-3-4 Lily 21 1111111
     * 按传递的参数替换
     * @param job
     */
    private static String parse2(Job job) {
        String inputParam = job.getInputParam();
        String command = job.getCommand();
        String[] params = inputParam.split(" ");
        String[] values = command.split(" ");
        StringBuffer sb= new StringBuffer();
        int length = params.length;
        int $i=0;
        for(String v:values){
            if(v.startsWith(MARK)){
                sb.append(" "+($i<length?params[$i]:v));
                $i++;
            }else{
                sb.append(" "+v);
            }
        }
        for(;$i<params.length;$i++){
            sb.append(" "+params[$i]);
        }
        return replaceSign(sb.toString(),job);
    }

    /**
     * 按默认值替换
     * @param job
     * @return
     */
    private static String parseDefault(Job job) {
        String command = job.getCommand();
        return replaceSign(command,job);
    }

    /**
     * 用job中的值替换command中的变量
     * @param command
     * @param job
     * @return
     */
    private static String replaceSign(String command,Job job) {
        int f$ = command.indexOf(MARK);
        if(f$<0){
            return command;
        }
        String subCmd = command.substring(0, f$);

        StringBuffer cmd=new StringBuffer(subCmd);

        String[] params = command.substring(f$).split(" ");
        for(String param:params){
            if(param.startsWith(MARK)){
                cmd.append(" "+replace(param,job).trim());
            }else{
                cmd.append(" "+param.trim());
            }
        }
        return cmd.toString();
    }

    /**
     * * $date: 当期日期 2017-08-01
     *  * $time: 当前时间 20180609210423
     *  * $user: 作业的用户
     *  * $jobId: 作业的ID
     *  * $pid: 作业记录对应的pid
     *  * $name: 作业的名称
     *
     * @param param
     * @param job
     * @return
     */
    private static String replace(String param, Job job) {
        switch (param){
            case "$date":
                return DateUtils.formatSimpleDate(new Date());
            case "$time":
                return DateUtils.formatSimpleDate(new Date());
            case "$user":
                return job.getUserId()+"";
            case "$jobId":
                return job.getJobId()+"";
            case "$name":
                return job.getJobName();
                default:
                    return "";
        }
    }
}
