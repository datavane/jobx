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

package org.opencron.common.job;

import com.alibaba.fastjson.annotation.JSONType;

import java.io.Serializable;
import java.util.*;

/**
 * Created by benjobs on 16/4/7.
 */
public class Monitor implements Serializable {

    private String time;

    private String memUsage;

    private String swap;

    private List<String> load = new ArrayList<String>(0);

    private String diskUsage;

    private String config;

    private String cpuData;

    private String top;

    //get...set..

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMemUsage() {
        return memUsage;
    }

    public void setMemUsage(String memUsage) {
        this.memUsage = memUsage;
    }

    public String getSwap() {
        return swap;
    }

    public void setSwap(String swap) {
        this.swap = swap;
    }

    public List<String> getLoad() {
        return load;
    }

    public void setLoad(List<String> load) {
        this.load = load;
    }

    public String getCpuData() {
        return cpuData;
    }

    public void setCpuData(String cpuData) {
        this.cpuData = cpuData;
    }

    public String getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(String diskUsage) {
        this.diskUsage = diskUsage;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }


    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    @Override
    public String toString() {
        return "Monitor{" +
                "time='" + time + '\'' +
                ", memUsage='" + memUsage + '\'' +
                ", swap='" + swap + '\'' +
                ", load=" + load.toString() +
                ", cpuData=" + cpuData +
                ", diskUsage=" + diskUsage +
                ", top=" + top +
                ", config=" + config +
                '}';
    }

    public static class Info implements Serializable {
        private List<Net> net = new ArrayList<Net>();
        private String disk;
        private Mem mem;
        private Swap swap;
        private Cpu cpu;
        private String load;
        private String conf;
        private String top;

        public List<Net> getNet() {
            return net;
        }

        public void setNet(List<Net> net) {
            this.net = net;
        }

        public String getDisk() {
            return disk;
        }

        public void setDisk(String disk) {
            this.disk = disk;
        }

        public Mem getMem() {
            return mem;
        }

        public void setMem(Mem mem) {
            this.mem = mem;
        }

        public Swap getSwap() {
            return swap;
        }

        public void setSwap(Swap swap) {
            this.swap = swap;
        }

        public Cpu getCpu() {
            return cpu;
        }

        public void setCpu(Cpu cpu) {
            this.cpu = cpu;
        }

        public String getLoad() {
            return load;
        }

        public void setLoad(String load) {
            this.load = load;
        }

        public String getConf() {
            return conf;
        }

        public void setConf(String conf) {
            this.conf = conf;
        }

        public String getTop() {
            return top;
        }

        public void setTop(String top) {
            this.top = top;
        }
    }


    public static class Iostat implements Serializable {
        private String device;// device: 设备
        private String rrqm;// rrqm/s:  每秒进行 merge 的读操作数目。即 rmerge/s
        private String wrqm;//  wrqm/s:  每秒进行 merge 的写操作数目。即 wmerge/s
        private String r;//  r/s:  每秒完成的读 I/O 设备次数。即 rio/s
        private String w;//  w/s:  每秒完成的写 I/O 设备次数。即 wio/s
        private String rkB;// rkB/s:  每秒读K字节数。是 rsect/s 的一半，因为每扇区大小为512字节
        private String wkB;//  wkB/s:  每秒写K字节数。是 wsect/s 的一半
        private String avgrq;// avgrq-sz:  平均每次设备I/O操作的数据大小 (扇区)
        private String avgqu;// avgqu-sz:  平均I/O队列长度
        private String await;// await:  平均每次设备I/O操作的等待时间 (毫秒)
        private String svctm;// svctm: 平均每次设备I/O操作的服务时间 (毫秒)
        private String util;//  %util:  一秒中有百分之多少的时间用于 I/O 操作，即被io消耗的cpu百分比

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public String getRrqm() {
            return rrqm;
        }

        public void setRrqm(String rrqm) {
            this.rrqm = rrqm;
        }

        public String getWrqm() {
            return wrqm;
        }

        public void setWrqm(String wrqm) {
            this.wrqm = wrqm;
        }

        public String getR() {
            return r;
        }

        public void setR(String r) {
            this.r = r;
        }

        public String getW() {
            return w;
        }

        public void setW(String w) {
            this.w = w;
        }

        public String getRkB() {
            return rkB;
        }

        public void setRkB(String rkB) {
            this.rkB = rkB;
        }

        public String getWkB() {
            return wkB;
        }

        public void setWkB(String wkB) {
            this.wkB = wkB;
        }

        public String getAvgrq() {
            return avgrq;
        }

        public void setAvgrq(String avgrq) {
            this.avgrq = avgrq;
        }

        public String getAvgqu() {
            return avgqu;
        }

        public void setAvgqu(String avgqu) {
            this.avgqu = avgqu;
        }

        public String getAwait() {
            return await;
        }

        public void setAwait(String await) {
            this.await = await;
        }

        public String getSvctm() {
            return svctm;
        }

        public void setSvctm(String svctm) {
            this.svctm = svctm;
        }

        public String getUtil() {
            return util;
        }

        public void setUtil(String util) {
            this.util = util;
        }
    }



    public static class Conf implements Serializable {
        private String hostname;
        private String os;
        private String kernel;
        private String machine;
        private String cpuinfo;

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getKernel() {
            return kernel;
        }

        public void setKernel(String kernel) {
            this.kernel = kernel;
        }

        public String getMachine() {
            return machine;
        }

        public void setMachine(String machine) {
            this.machine = machine;
        }

        public String getCpuinfo() {
            return cpuinfo;
        }

        public void setCpuinfo(String cpuinfo) {
            this.cpuinfo = cpuinfo;
        }
    }

    public static class Cpu implements Serializable {
        private String id2;
        private String id1;
        private String total2;
        private String total1;
        private String detail;


        public String getId2() {
            return id2;
        }

        public void setId2(String id2) {
            this.id2 = id2;
        }

        public String getId1() {
            return id1;
        }

        public void setId1(String id1) {
            this.id1 = id1;
        }

        public String getTotal2() {
            return total2;
        }

        public void setTotal2(String total2) {
            this.total2 = total2;
        }

        public String getTotal1() {
            return total1;
        }

        public void setTotal1(String total1) {
            this.total1 = total1;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }

    public static class Mem implements Serializable {
        private float total;
        private float used;

        public float getTotal() {
            return total;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public float getUsed() {
            return used;
        }

        public void setUsed(float used) {
            this.used = used;
        }
    }

    public static class Net implements Serializable {
        private String name;
        private float read;
        private float write;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getRead() {
            return read;
        }

        public void setRead(float read) {
            this.read = read;
        }

        public float getWrite() {
            return write;
        }

        public void setWrite(float write) {
            this.write = write;
        }
    }

    public static class Swap implements Serializable {
        private float total;
        private float free;

        public float getTotal() {
            return total;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public float getFree() {
            return free;
        }

        public void setFree(float free) {
            this.free = free;
        }
    }

    @JSONType(orders={"pid","user","virt","res","cpu","mem","time","command"})
    public static class Top implements Serializable {

        private String pid;
        private String user;
        private String virt;
        private String res;
        private String cpu;
        private String mem;
        private String time;
        private String command;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getVirt() {
            return virt;
        }

        public void setVirt(String virt) {
            this.virt = virt;
        }

        public String getRes() {
            return res;
        }

        public void setRes(String res) {
            this.res = res;
        }

        public String getCpu() {
            return cpu;
        }

        public void setCpu(String cpu) {
            this.cpu = cpu;
        }

        public String getMem() {
            return mem;
        }

        public void setMem(String mem) {
            this.mem = mem;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        @Override
        public String toString() {
            return "Top{" +
                    "pid='" + pid + '\'' +
                    ", user='" + user + '\'' +
                    ", virt='" + virt + '\'' +
                    ", res='" + res + '\'' +
                    ", cpu='" + cpu + '\'' +
                    ", mem='" + mem + '\'' +
                    ", time='" + time + '\'' +
                    ", command='" + command + '\'' +
                    '}';
        }
    }
}
