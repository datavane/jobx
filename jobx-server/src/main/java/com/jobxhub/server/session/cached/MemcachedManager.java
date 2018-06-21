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
package com.jobxhub.server.session.cached;


import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.ConnectionObserver;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.transcoders.Transcoder;

/**
 * @author benjobs
 */
public class MemcachedManager implements CachedManager {

    public static final int DEFAULT_TIMEOUT = 5;

    public static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.SECONDS;

    private MemcachedClient memcachedClient;

    private int expire;

    public void addObserver(ConnectionObserver obs) {
        memcachedClient.addObserver(obs);
    }

    public void removeObserver(ConnectionObserver obs) {
        memcachedClient.removeObserver(obs);
    }

    @Override
    public <T> T get(Object key, Class<T> clazz) {
        return (T) memcachedClient.get(key.toString());
    }

    @Override
    public void delete(Object key) {
        memcachedClient.delete(key.toString());
    }

    @Override
    public void set(Object key, Object object) {
        memcachedClient.set(key.toString(), this.expire, object);
    }

    @Override
    public <T> T remove(Object key, Class<T> clazz) {
        T t = (T) memcachedClient.get(key.toString());
        this.delete(key);
        return t;
    }

    public Object asyncGet(String key) {
        Object obj = null;
        Future<Object> f = memcachedClient.asyncGet(key);
        try {
            obj = f.get(DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        } catch (Exception e) {
            f.cancel(false);
        }
        return obj;
    }


    public boolean replace(String key, Object value) {
        Future<Boolean> f = memcachedClient.replace(key, expire, value);
        return getBooleanValue(f);
    }

    public boolean flush() {
        Future<Boolean> f = memcachedClient.flush();
        return getBooleanValue(f);
    }

    public Map<String, Object> getMulti(Collection<String> keys) {
        return memcachedClient.getBulk(keys);
    }

    public Map<String, Object> getMulti(String[] keys) {
        return memcachedClient.getBulk(keys);
    }

    public Map<String, Object> asyncGetMulti(Collection<String> keys) {
        Map<String, Object> map = null;
        Future<Map<String, Object>> f = memcachedClient.asyncGetBulk(keys);
        try {
            map = f.get(DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        } catch (Exception e) {
            f.cancel(false);
        }
        return map;
    }

    public Map<String, Object> asyncGetMulti(String[] keys) {
        Map<String, Object> map = null;
        Future<Map<String, Object>> f = memcachedClient.asyncGetBulk(keys);
        try {
            map = f.get(DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        } catch (Exception e) {
            f.cancel(false);
        }
        return map;
    }

    // ---- increment & decrement Start ----//
    public long increment(String key, int by, long defaultValue, int expire) {
        return memcachedClient.incr(key, by, defaultValue, expire);
    }

    public long increment(String key, int by) {
        return memcachedClient.incr(key, by);
    }

    public long decrement(String key, int by, long defaultValue, int expire) {
        return memcachedClient.decr(key, by, defaultValue, expire);
    }

    public long decrement(String key, int by) {
        return memcachedClient.decr(key, by);
    }

    public long asyncIncrement(String key, int by) {
        Future<Long> f = memcachedClient.asyncIncr(key, by);
        return getLongValue(f);
    }

    public long asyncDecrement(String key, int by) {
        Future<Long> f = memcachedClient.asyncDecr(key, by);
        return getLongValue(f);
    }

    // ---- increment & decrement End ----//
    public void printStats() throws IOException {
        printStats(null);
    }

    public void printStats(OutputStream stream) throws IOException {
        Map<SocketAddress, Map<String, String>> statMap = memcachedClient.getStats();
        if (stream == null) {
            stream = System.out;
        }
        StringBuffer buf = new StringBuffer();
        Set<SocketAddress> addrSet = statMap.keySet();
        Iterator<SocketAddress> iter = addrSet.iterator();
        while (iter.hasNext()) {
            SocketAddress addr = iter.next();
            buf.append(addr.toString() + "/n");
            Map<String, String> stat = statMap.get(addr);
            Set<String> keys = stat.keySet();
            Iterator<String> keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                String value = stat.get(key);
                buf.append("  key=" + key + ";value=" + value + "/n");
            }
            buf.append("/n");
        }
        stream.write(buf.toString().getBytes());
        stream.flush();
    }

    public Transcoder getTranscoder() {
        return memcachedClient.getTranscoder();
    }

    private long getLongValue(Future<Long> f) {
        try {
            Long l = f.get(DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
            return l.longValue();
        } catch (Exception e) {
            f.cancel(false);
        }
        return -1;
    }

    private boolean getBooleanValue(Future<Boolean> f) {
        try {
            Boolean bool = f.get(DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
            return bool.booleanValue();
        } catch (Exception e) {
            f.cancel(false);
            return false;
        }
    }

    public MemcachedClient getMemcachedClient() {
        return memcachedClient;
    }

    public void setMemcachedClient(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }
}