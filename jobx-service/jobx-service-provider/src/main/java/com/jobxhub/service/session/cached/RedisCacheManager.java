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

package com.jobxhub.service.session.cached;

import com.jobxhub.common.ext.ExtensionLoader;
import com.jobxhub.common.serialize.Serializer;
import com.jobxhub.common.util.CommonUtils;
import org.springframework.cache.Cache;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author benjobs
 */
public class RedisCacheManager implements Cache,CachedManager {

    private Serializer serializer = ExtensionLoader.load(Serializer.class);

    /**
     * Redis
     */

    private RedisTemplate redisTemplate;

    /**
     * 缓存名称
     */
    private String name;

    /**
     * 超时时间
     */
    private long expire;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.redisTemplate;
    }

    @Override
    public <T> T get(Object key, final Class<T> type) {
        if (CommonUtils.isEmpty(key) || null == type) {
            return null;
        } else {
            final String finalKey;
            final Class<T> finalType = type;
            if (key instanceof String) {
                finalKey = (String) key;
            } else {
                finalKey = key.toString();
            }
            final Object object = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    byte[] key = finalKey.getBytes();
                    byte[] value = connection.get(key);
                    if (value == null) {
                        return null;
                    }
                    try {
                        return serializer.deserialize(value, finalType);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
            if (finalType != null && finalType.isInstance(object) && null != object) {
                return (T) object;
            } else {
                return null;
            }
        }
    }

    @Override
    public void delete(Object key) {
        this.evict(key);
    }

    @Override
    public void set(Object key, Object object) {
        this.put(key,object);
    }

    @Override
    public <T> T remove(Object key, final Class<T> type) {
        T t = get(key, type);
        evict(key);
        return t;
    }

    @Override
    public void put(final Object key, final Object value) {
        if (CommonUtils.isEmpty(key) || CommonUtils.isEmpty(value)) {
            return;
        } else {
            final String finalKey;
            if (key instanceof String) {
                finalKey = (String) key;
            } else {
                finalKey = key.toString();
            }
            if (CommonUtils.notEmpty(finalKey)) {
                final Object finalValue = value;
                redisTemplate.execute(new RedisCallback<Boolean>() {
                    @Override
                    public Boolean doInRedis(RedisConnection connection) {
                        try {
                            byte[] data = serializer.serialize(finalValue);
                            connection.set(finalKey.getBytes(), data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // 设置超时间
                        connection.expire(finalKey.getBytes(), getExpire());
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public ValueWrapper putIfAbsent(Object o, Object o1) {
        return null;
    }

    /*
     * 根据Key 删除缓存
     */
    @Override
    public void evict(Object key) {
        if (null != key) {
            final String finalKey;
            if (key instanceof String) {
                finalKey = (String) key;
            } else {
                finalKey = key.toString();
            }
            if (CommonUtils.notEmpty(finalKey)) {
                redisTemplate.execute(new RedisCallback<Long>() {
                    @Override
                    public Long doInRedis(RedisConnection connection) throws DataAccessException {
                        return connection.del(finalKey.getBytes());
                    }
                });
            }
        }
    }


    @Override
    public void clear() {
    }

    @Override
    @Deprecated
    public ValueWrapper get(Object o) {
        return null;
    }

    @Override
    public <T> T get(Object o, Callable<T> callable) {
        return null;
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public List<String> keys(String pattern) {
        return (List<String>) redisTemplate.keys(pattern);
    }




}

