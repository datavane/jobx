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

package com.jobxhub.common.ext;

import com.jobxhub.common.Constants;
import lombok.extern.slf4j.Slf4j;
import com.jobxhub.common.util.CommonUtils;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import com.jobxhub.common.util.collection.HashMap;
import java.util.concurrent.ConcurrentMap;

import static com.jobxhub.common.util.AssertUtils.checkNotNull;

/**
 * @author benjobs...
 */
@Slf4j
public class ExtensionLoader<T> {

    private Class<T> type;

    private ClassLoader loader;

    private SPI spi;

    private final Map<String, Class<?>> EXTENSION_SPI = new HashMap<String, Class<?>>();

    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new HashMap<Class<?>, ExtensionLoader<?>>();

    public static <T> T load(Class<T> type) {
        return getExtensionLoader(type).getExtension(null);
    }

    public static <T> T load(Class<T> type, String spiName) {
        return getExtensionLoader(type).getExtension(spiName);
    }

    private static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader(type, Thread.currentThread().getContextClassLoader()));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    public ExtensionLoader(Class<T> type, ClassLoader loader) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type(" + type + ") is not interface!");
        }
        if (!withExtensionAnnotation(type)) {
            throw new IllegalArgumentException("Extension type(" + type + ") is not extension, because WITHOUT @" + SPI.class.getSimpleName() + " Annotation!");
        }
        this.type = checkNotNull(type, "type interface cannot be null");
        this.loader = checkNotNull(loader, "Extension loader == null");
        this.spi = this.type.getAnnotation(SPI.class);
        loadFile();
    }

    public T getExtension(String spiName) {
        try {
            spiName = getSpiKey(spiName);
            Class<?> instanceClass;
            //spi注解上是否指定了实现类的key
            if (CommonUtils.isEmpty(spi.value())) {
                instanceClass = EXTENSION_SPI.get(spiName);
            } else {
                //当前的获取方法是否指定了spi实现类的Key
                if (CommonUtils.notEmpty(spiName)) {
                    instanceClass = EXTENSION_SPI.get(getSpiKey(spi.value()));
                } else {
                    instanceClass = EXTENSION_SPI.get(spi.value());
                }
            }
            if (instanceClass != null) {
                Object instance = instanceClass.newInstance();
                this.type.cast(instance);
                return (T) instance;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        throw new IllegalArgumentException(this.type.getName() + " impl could not be found");
    }

    private void loadFile() {
        String fileName = Constants.META_INF_DIR + this.type.getName();
        try {
            //for AppClassLoader
            Enumeration<URL> urls = ClassLoader.getSystemResources(fileName);
            if (urls != null) {
                if (!urls.hasMoreElements()) {
                    //for WebAppClassLoader
                    urls = this.loader.getResources(fileName);
                }
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    Scanner scanner = null;
                    try {
                        scanner = new Scanner(new InputStreamReader(url.openStream(), Charset.forName(Constants.CHARSET_UTF8)));
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            if (CommonUtils.notEmpty(line)) {
                                //已经注释的结构的统统跳过.
                                if (line.indexOf("#") == 0) {
                                    continue;
                                }

                                line = line.trim();
                                try {
                                    String[] args = line.split("=");
                                    String spiName = null;
                                    String spiImpl = null;

                                    if (args.length == 1) {
                                        if (CommonUtils.isEmpty(this.spi.value())) {
                                            //default SpiImpl...
                                            spiImpl = args[0].trim();
                                        }
                                    } else if (args.length == 2) {
                                        String name = args[0].trim();
                                        line = args[1].trim();
                                        if (CommonUtils.notEmpty(name, line)) {
                                            spiName = name;
                                            spiImpl = line;
                                        }
                                    } else {
                                        throw new IllegalStateException("invalid SPI configuration:" + line + "please check config: " + url);
                                    }

                                    if (spiImpl != null) {
                                        Class clazz = Class.forName(spiImpl, false, this.loader);
                                        if (!this.type.isAssignableFrom(clazz)) {
                                            throw new IllegalStateException("Error when load extension class(interface: " +
                                                    this.type + ", class line: " + clazz.getName() + "), class "
                                                    + clazz.getName() + "is not subtype of interface.");
                                        }
                                        this.EXTENSION_SPI.put(getSpiKey(spiName), clazz);
                                    }
                                } catch (Throwable t) {
                                    throw new IllegalStateException("Failed to load extension class(interface: " + type + ", class line: " + line + ") in " + url + ", cause: " + t.getMessage(), t);
                                }
                            }
                        }
                    } catch (Throwable t) {
                        log.error("Exception when load extension class(interface: " + type + ", class file: " + url + ") in " + url, t);
                    } finally {
                        scanner.close();
                    }
                } // end of while urls
            }
        } catch (Throwable t) {
            log.error("Exception when load extension class(interface: " + type + ", description file: " + fileName + ").", t);
        }

    }

    private String getSpiKey(String spiName) {
        if (spiName != null) {
            return spiName.concat("_").concat(this.type.getName());
        }
        return this.type.getName();
    }

    private static <T> boolean withExtensionAnnotation(Class<T> type) {
        return type.isAnnotationPresent(SPI.class);
    }

}
