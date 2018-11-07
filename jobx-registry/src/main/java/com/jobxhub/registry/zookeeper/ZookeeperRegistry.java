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

package com.jobxhub.registry.zookeeper;

import com.jobxhub.common.exception.RpcException;
import org.slf4j.LoggerFactory;
import com.jobxhub.common.util.collection.ConcurrentHashSet;
import com.jobxhub.registry.URL;
import com.jobxhub.registry.api.Registry;
import org.slf4j.Logger;

import java.util.*;

/**
 * ZookeeperRegistry
 *
 * @author benjobs
 */
public class ZookeeperRegistry implements Registry {

    private final static Logger logger = LoggerFactory.getLogger(ZookeeperRegistry.class);

    private final Set<URL> failedRegistered = new ConcurrentHashSet<URL>();

    private final ZookeeperClient zkClient;

    private final Set<URL> registered = new ConcurrentHashSet<URL>();

    private URL registryUrl;

    public ZookeeperRegistry(URL url, ZookeeperTransporter zookeeperTransporter) {
        if (url.isAnyHost()) {
            throw new IllegalStateException("[JobX] registry address == null");
        }
        this.registryUrl = url;
        zkClient = zookeeperTransporter.connect(url);
        zkClient.addStateListener(new StateListener() {
            public void stateChanged(int state) {
                if (state == RECONNECTED) {
                    try {
                        recover();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });
        this.registered.add(url);
    }

    @Override
    public boolean isAvailable() {
        return zkClient.isConnected();
    }

    @Override
    public void destroy() {
        try {
            zkClient.close();
        } catch (Exception e) {
            logger.warn("[JobX] Failed to close zookeeper client " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public void recover() throws Exception {
        // register
        Set<URL> recoverRegistered = new HashSet<URL>(getRegistered());
        if (!recoverRegistered.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("[JobX] Recover register url " + recoverRegistered);
            }
            for (URL url : recoverRegistered) {
                failedRegistered.add(url);
            }
        }
    }

    @Override
    public void register(String path, boolean ephemeral) {
        try {
            zkClient.create(path, ephemeral);
        } catch (Throwable e) {
            throw new RpcException("[JobX] Failed to register " + getUrl() + " to zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public void unRegister(String path) {
        try {
            zkClient.delete(path);
        } catch (Throwable e) {
            throw new RpcException("[JobX] Failed to unregister " + getUrl() + " to zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public ZookeeperClient getClient() {
        return this.zkClient;
    }

    public URL getUrl() {
        return registryUrl;
    }

    protected void setUrl(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("[JobX] registry url == null");
        }
        this.registryUrl = url;
    }

    public Set<URL> getRegistered() {
        return registered;
    }

}