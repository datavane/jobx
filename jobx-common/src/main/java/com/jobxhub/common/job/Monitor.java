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

package com.jobxhub.common.job;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Swap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by benjobs on 16/4/7.
 */
public class Monitor implements Serializable {

    private List<CPU> cpu;//cpu

    private Mem mem;//内存

    private Load load;//负载

    public List<CPU> getCpu() {
        return cpu;
    }

    public void setCpu(List<CPU> cpu) {
        this.cpu = cpu;
    }

    public Mem getMem() {
        return mem;
    }

    public void setMem(Mem mem) {
        this.mem = mem;
    }


    public Load getLoad() {
        return load;
    }

    public void setLoad(Load load) {
        this.load = load;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(0);
        if (this.cpu != null) {
            map.put("cpu", this.cpu.toString());
        }
        if (this.mem != null) {
            map.put("mem", this.mem.toString());
        }
        if (this.load != null) {
            map.put("load", this.load.toString());
        }
        return map;
    }

    public static class CPU implements Serializable {

        private int index;
        private String vendor = null;
        private String model = null;
        private int mhz = 0;
        private long cacheSize = 0L;
        private int totalCores = 0;
        private int totalSockets = 0;
        private int coresPerSocket = 0;
        private double user;
        private double sys;
        private double nice;
        private double idle;
        private double wait;
        private double irq;
        private double softIrq;
        private double stolen;
        private double combined;

        public CPU(int index, CpuInfo info, CpuPerc perc) {
            this.index = index;
            this.cacheSize = info.getCacheSize();
            this.coresPerSocket = info.getCoresPerSocket();
            this.totalCores = info.getTotalCores();
            this.totalSockets = info.getTotalSockets();
            this.mhz = info.getMhz();
            this.model = info.getModel();
            this.vendor = info.getVendor();

            this.user = perc.getUser();
            this.sys = perc.getSys();
            this.nice = perc.getNice();
            this.idle = perc.getIdle();
            this.wait = perc.getWait();
            this.irq = perc.getIrq();
            this.softIrq = perc.getSoftIrq();
            this.stolen = perc.getStolen();
            this.combined = perc.getCombined();
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getVendor() {
            return vendor;
        }

        public void setVendor(String vendor) {
            this.vendor = vendor;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public int getMhz() {
            return mhz;
        }

        public void setMhz(int mhz) {
            this.mhz = mhz;
        }

        public long getCacheSize() {
            return cacheSize;
        }

        public void setCacheSize(long cacheSize) {
            this.cacheSize = cacheSize;
        }

        public int getTotalCores() {
            return totalCores;
        }

        public void setTotalCores(int totalCores) {
            this.totalCores = totalCores;
        }

        public int getTotalSockets() {
            return totalSockets;
        }

        public void setTotalSockets(int totalSockets) {
            this.totalSockets = totalSockets;
        }

        public int getCoresPerSocket() {
            return coresPerSocket;
        }

        public void setCoresPerSocket(int coresPerSocket) {
            this.coresPerSocket = coresPerSocket;
        }

        public double getUser() {
            return user;
        }

        public void setUser(double user) {
            this.user = user;
        }

        public double getSys() {
            return sys;
        }

        public void setSys(double sys) {
            this.sys = sys;
        }

        public double getNice() {
            return nice;
        }

        public void setNice(double nice) {
            this.nice = nice;
        }

        public double getIdle() {
            return idle;
        }

        public void setIdle(double idle) {
            this.idle = idle;
        }

        public double getWait() {
            return wait;
        }

        public void setWait(double wait) {
            this.wait = wait;
        }

        public double getIrq() {
            return irq;
        }

        public void setIrq(double irq) {
            this.irq = irq;
        }

        public double getSoftIrq() {
            return softIrq;
        }

        public void setSoftIrq(double softIrq) {
            this.softIrq = softIrq;
        }

        public double getStolen() {
            return stolen;
        }

        public void setStolen(double stolen) {
            this.stolen = stolen;
        }

        public double getCombined() {
            return combined;
        }

        public void setCombined(double combined) {
            this.combined = combined;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"index\":" + index +
                    ",\"vendor\":\"" + vendor + "\"" +
                    ",\"model\":'" + model + "\"" +
                    ",\"mhz\":" + mhz +
                    ",\"cacheSize\":" + cacheSize +
                    ",\"totalCores\":" + totalCores +
                    ",\"totalSockets\":" + totalSockets +
                    ",\"coresPerSocket\":" + coresPerSocket +
                    ",\"user\":" + user +
                    ",\"sys\":" + sys +
                    ",\"nice\":" + nice +
                    ",\"idle\":" + idle +
                    ",\"wait\":" + wait +
                    ",\"irq\":" + irq +
                    ",\"softIrq\":" + softIrq +
                    ",\"stolen\":" + stolen +
                    ",\"combined\":" + combined +
                    '}';
        }
    }

    public static class Mem implements Serializable {
        private long total = 0L;
        private long ram = 0L;
        private long used = 0L;
        private long free = 0L;

        private long swapTotal = 0L;
        private long swapUsed = 0L;
        private long swapFree = 0L;

        private long actualUsed = 0L;
        private long actualFree = 0L;
        private double usedPercent = 0.0D;
        private double freePercent = 0.0D;

        public Mem(org.hyperic.sigar.Mem mem, Swap swap) {
            this.total = mem.getTotal() / 1024;
            this.ram = mem.getRam();
            this.used = mem.getUsed() / 1024;
            this.free = mem.getFree() / 1024;
            this.actualUsed = mem.getActualUsed() / 1024;
            this.actualFree = mem.getActualFree() / 1024;
            this.usedPercent = mem.getUsedPercent() / 1024;
            this.freePercent = mem.getFreePercent() / 1024;
            this.swapTotal = swap.getTotal() / 1024;
            this.swapFree = swap.getFree() / 1024;
            this.swapUsed = swap.getUsed() / 1024;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public long getRam() {
            return ram;
        }

        public void setRam(long ram) {
            this.ram = ram;
        }

        public long getUsed() {
            return used;
        }

        public void setUsed(long used) {
            this.used = used;
        }

        public long getFree() {
            return free;
        }

        public void setFree(long free) {
            this.free = free;
        }

        public long getSwapTotal() {
            return swapTotal;
        }

        public void setSwapTotal(long swapTotal) {
            this.swapTotal = swapTotal;
        }

        public long getSwapUsed() {
            return swapUsed;
        }

        public void setSwapUsed(long swapUsed) {
            this.swapUsed = swapUsed;
        }

        public long getSwapFree() {
            return swapFree;
        }

        public void setSwapFree(long swapFree) {
            this.swapFree = swapFree;
        }

        public long getActualUsed() {
            return actualUsed;
        }

        public void setActualUsed(long actualUsed) {
            this.actualUsed = actualUsed;
        }

        public long getActualFree() {
            return actualFree;
        }

        public void setActualFree(long actualFree) {
            this.actualFree = actualFree;
        }

        public double getUsedPercent() {
            return usedPercent;
        }

        public void setUsedPercent(double usedPercent) {
            this.usedPercent = usedPercent;
        }

        public double getFreePercent() {
            return freePercent;
        }

        public void setFreePercent(double freePercent) {
            this.freePercent = freePercent;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"total\":" + total +
                    ",\"ram\":" + ram +
                    ",\"used\":" + used +
                    ",\"free\":" + free +
                    ",\"swapTotal\":" + swapTotal +
                    ",\"swapUsed\":" + swapUsed +
                    ",\"swapFree\":" + swapFree +
                    ",\"actualUsed\":" + actualUsed +
                    ",\"actualFree\":" + actualFree +
                    ",\"usedPercent\":" + usedPercent +
                    ",\"freePercent\":" + freePercent +
                    '}';
        }
    }

    public static class Load implements Serializable {

        private double one;//1分钟的平均负载
        private double five;//5分钟的平均负载
        private double fifteen;//15分钟的评价负载

        public Load(double[] loads) {
            this.one = loads[0];
            this.five = loads[1];
            this.fifteen = loads[2];
        }

        public double getOne() {
            return one;
        }

        public void setOne(double one) {
            this.one = one;
        }

        public double getFive() {
            return five;
        }

        public void setFive(double five) {
            this.five = five;
        }

        public double getFifteen() {
            return fifteen;
        }

        public void setFifteen(double fifteen) {
            this.fifteen = fifteen;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"one\":" + one +
                    ",\"five\":" + five +
                    ",\"fifteen\":" + fifteen +
                    '}';
        }
    }

}
