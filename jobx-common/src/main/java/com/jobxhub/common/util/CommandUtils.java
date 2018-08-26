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

package com.jobxhub.common.util;


import com.jobxhub.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:benjobs@qq.com">B e n</a>
 * @name:CommonUtil
 * @version: 1.0.0
 * @company: com.jobxhub
 * @description: 常用工具类
 * @date: 2012-10-9 pa 18:03<br/><br/>
 **/
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class CommandUtils implements Serializable {

    private static final long serialVersionUID = 6458428317155311192L;

    private static Logger logger = LoggerFactory.getLogger(CommandUtils.class);

    public static String DEFAULT_USER = "root";

    public static String BASH_SCHEAM = "#!/bin/bash";


    public static int getPID(Process process) {
        int processId = 0;
        try {
            Field field = ReflectUtils.getField(process.getClass(), "pid");
            processId = field.getInt(process);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return processId;
    }

    public static Integer getPIDByPP(Process process) {
        try {
            String cmd = String.format("ps -ef|awk '{if($3~/%d/) print $2}'",getPID(process));
            return CommonUtils.toInt(execute(cmd));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String execute(String command) {
        Process process = null;
        StringBuffer buffer = new StringBuffer();
        try {
            process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (process != null) {
            try {
                process.getErrorStream().close();
                process.getInputStream().close();
                process.getOutputStream().close();
            } catch (Exception ee) {
            }
        }
        return buffer.toString();
    }

    public static List<String> getCommandLine(String command) {
        ArrayList<String> commands = new ArrayList<String>();
        int index = 0;

        StringBuffer buffer = new StringBuffer(command.length());

        boolean isApos = false;
        boolean isQuote = false;
        while (index < command.length()) {
            char c = command.charAt(index);
            switch (c) {
                case ' ':
                    if (!isQuote && !isApos) {
                        String arg = buffer.toString();
                        buffer = new StringBuffer(command.length() - index);
                        if (arg.length() > 0) {
                            commands.add(arg);
                        }
                    } else {
                        buffer.append(c);
                    }
                    break;
                case '\'':
                    if (!isQuote) {
                        isApos = !isApos;
                    } else {
                        buffer.append(c);
                    }
                    break;
                case '"':
                    if (!isApos) {
                        isQuote = !isQuote;
                    } else {
                        buffer.append(c);
                    }
                    break;
                default:
                    buffer.append(c);
            }

            index++;
        }

        if (buffer.length() > 0) {
            String arg = buffer.toString();
            commands.add(arg);
        }

        return commands;
    }


    public static void write(File shellFile, String command) {
        try {
            if (!shellFile.exists()) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(shellFile)));
                out.write(BASH_SCHEAM);
                out.write("\n\n");
                out.write(command);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public static int chown(boolean r,String user,String group,File file) throws IOException, InterruptedException {
        return runAsExecUser(DEFAULT_USER,String.format("chown %s %s:%s %s",(r?"-R":""),user,group,file.getAbsolutePath()));
    }

    private static int runAsExecUser(final String execUser,final String command) throws IOException, InterruptedException {
        String execCmd = Constants.JOBX_EXECUTE_AS_USER_LIB
                .concat(IOUtils.BLANK_CHAR)
                .concat(execUser)
                .concat(IOUtils.BLANK_CHAR)
                .concat(command);
        final Process process = Runtime.getRuntime().exec(execCmd);
        return process.waitFor();
    }


}




