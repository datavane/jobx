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

import org.hyperic.sigar.*;
import com.jobxhub.common.job.Monitor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;

/**
 * Created by benjobs on 16/4/7.
 */
public class MonitorService {

    private Logger logger = LoggerFactory.getLogger(MonitorService.class);

    private Sigar sigar;

    public Monitor monitor() throws SigarException {
        sigar = new Sigar();
        Monitor monitor = new Monitor();
        monitor.setCpu(getCup());
        monitor.setMem(getMem());
        monitor.setLoad(getLoad());
        return monitor;
    }

    public List<Monitor.CPU> getCup() throws SigarException {
        CpuInfo infos[] = sigar.getCpuInfoList();
        CpuPerc cpuList[] = sigar.getCpuPercList();
        List<Monitor.CPU> cpus = new ArrayList<Monitor.CPU>();
        for (int i = 0; i < infos.length; i++) {
            cpus.add(new Monitor.CPU(i, infos[i], cpuList[i]));
        }
        return cpus;
    }

    public Monitor.Mem getMem() throws SigarException {
        return new Monitor.Mem(sigar.getMem(), sigar.getSwap());
    }

    public Monitor.Load getLoad() throws SigarException {
        return new Monitor.Load(sigar.getLoadAverage());
    }
}
