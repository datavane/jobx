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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacUtils {

    private static String macAddressStr  = null;

    private static final String[] windowsCommand = { "ipconfig", "/all" };
    private static final String[] linuxCommand   = { "/sbin/ifconfig", "-a" };
    private static final Pattern  macPattern     = Pattern.compile(".*((:?[0-9a-f]{2}[-:]){5}[0-9a-f]{2}).*", Pattern.CASE_INSENSITIVE);

    public final static List<String> getMacAddressList() throws IOException {
        final ArrayList<String> macAddressList = new ArrayList<String>();
        final String command[];

        if (CommonUtils.isWindowOs()) {
            command = windowsCommand;
        } else if (CommonUtils.isLinuxOs()) {
            command = linuxCommand;
        } else {
            throw new IOException("Unknow operating system:" + CommonUtils.getOsName());
        }

        final Process process = Runtime.getRuntime().exec(command);

        BufferedReader bufReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        for (String line; (line = bufReader.readLine()) != null ;) {
            Matcher matcher = macPattern.matcher(line);
            if (matcher.matches()) {
                //macAddressList.add(matcher.group(1));
                macAddressList.add(matcher.group(1).replaceAll("[-:]", ""));//去掉MAC中的“-”
            }
        }
        process.destroy();
        bufReader.close();
        return macAddressList;
    }

    public static String getMacAddress() {
        if (macAddressStr == null || macAddressStr.equals("")) {
            StringBuffer sb = new StringBuffer(); //存放多个网卡地址用，目前只取一个非0000000000E0隧道的值
            try {
                List<String> macList = getMacAddressList();
                for (Iterator<String> iter = macList.iterator() ; iter.hasNext() ;) {
                    String amac = iter.next();
                    if (!amac.equals("0000000000E0")) {
                        sb.append(amac);
                        break;
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            macAddressStr = sb.toString();
        }
        return macAddressStr;
    }

    public static void main(String[] args) {
        System.out.println(MacUtils.getMacAddress());
    }
}