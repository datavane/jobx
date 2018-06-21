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

import com.jobxhub.common.exception.UnknownException;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

public class MacUtils {

    public static String getMac() {
        try {
            byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
            StringBuffer buffer = new StringBuffer("");
            for (int i = 0; i < mac.length; i++) {
                if (i > 0) {
                    buffer.append("-");
                }
                int temp = mac[i] & 0xff;
                String str = Integer.toHexString(temp);
                if (str.length() == 1) {
                    buffer.append("0" + str);
                } else {
                    buffer.append(str);
                }
            }
            return buffer.toString();
        } catch (Exception e) {
            throw new UnknownException("[JobX] getMacAddress error");
        }
    }

    /**
     * 获取本机所有的网卡(过滤虚拟网卡)
     *
     * @return
     */
    public static Set<String> getAllMac() {
        List<String> list = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface network = enumeration.nextElement();
                if (network == null || network.getHardwareAddress() == null || network.isLoopback() || network.isVirtual() || !network.isUp()) {
                    continue;
                }
                byte[] address = network.getHardwareAddress();
                if (address==null) continue;
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < address.length; i++) {
                    if (i > 0) {
                        buffer.append("-");
                    }
                    String str = Integer.toHexString(address[i] & 0xff);
                    if (str.length() == 1) {
                        buffer.append("0" + str);
                    } else {
                        buffer.append(str);
                    }
                }
                list.add(buffer.toString());
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (list.isEmpty()) {
            throw new UnknownException("[JobX] getAllMac error");
        }
        //按照字典顺序排序
        return new TreeSet<String>(list);
    }

    //获取机器唯一标识
    public static String getMachineId() {
        String unId = StringUtils.join(MacUtils.getAllMac(), "");
        return DigestUtils.md5Hex(unId);
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        System.out.println(MacUtils.getMac());
        System.out.println(MacUtils.getAllMac());
        System.out.println(MacUtils.getMachineId());
    }
}