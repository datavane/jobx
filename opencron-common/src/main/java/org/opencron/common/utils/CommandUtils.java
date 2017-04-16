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

package org.opencron.common.utils;


import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.*;


/**
 * @author <a href="mailto:benjobs@qq.com">B e n</a>
 * @name:CommonUtil
 * @version: 1.0.0
 * @company: org.opencron
 * @description: 常用工具类
 * @date: 2012-10-9 pa 18:03<br/><br/>
 **/
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class CommandUtils implements Serializable {

    private static final long serialVersionUID = 6458428317155311192L;

    public static File createShellFile(String command, String shellFileName) {
        String dirPath = IOUtils.getTempFolderPath();
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();

        String tempShellFilePath = dirPath + File.separator + shellFileName + ".sh";
        File shellFile = new File(tempShellFilePath);
        try {
            if (!shellFile.exists()) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(tempShellFilePath)));
                out.write("#!/bin/bash\n" + command);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return shellFile;
        }
    }

    public static String executeCommand(String shellFileName, String command, String args) {
        File shellFile = createShellFile(command, shellFileName);
        return executeShell(shellFile, args);
    }

    public static String executeShell(File shellFile, String... args) {
        String info = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {

            String params=" ";
            if (CommonUtils.notEmpty(args)) {
                for (String p : args) {
                    params += p + " ";
                }
            }

            CommandLine commandLine = CommandLine.parse("/bin/bash +x " + shellFile.getAbsolutePath() + params);
            DefaultExecutor exec = new DefaultExecutor();
            exec.setExitValues(null);
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, outputStream);
            exec.setStreamHandler(streamHandler);

            exec.execute(commandLine);
            info = outputStream.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return info;
        }
    }

}


