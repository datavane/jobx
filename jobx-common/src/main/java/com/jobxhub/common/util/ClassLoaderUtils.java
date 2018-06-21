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


import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author benjobs
 */
public final class ClassLoaderUtils {

    /**
     * URLClassLoader的addURL方法
     */
    private static Method addURL = initAddMethod();

    private static URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    private static Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            add.setAccessible(true);
            return add;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadPath(String filepath) {
        File file = new File(filepath);
        loopFiles(file);
    }

    private static void loadResourceDir(String filepath) {
        File file = new File(filepath);
        loopDirs(file);
    }


    private static void loopDirs(File file) {
        // 资源文件只加载路径
        if (file.isDirectory()) {
            addURL(file);
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopDirs(tmp);
            }
        }
    }


    private static void loopFiles(File file) {
        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopFiles(tmp);
            }
        } else {
            if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".zip")) {
                addURL(file);
            }
        }
    }


    private static void addURL(File file) {
        try {
            addURL.invoke(classloader, new Object[]{file.toURI().toURL()});
        } catch (Exception e) {
        }
    }

    public static void loadJar(String jarFilePath) {
        File jarFile = new File(jarFilePath);
        if (!jarFile.exists()) {
            throw new IllegalArgumentException("[JobX] jarFilePath:" + jarFilePath + " is not exists");
        }
        if (jarFile.isFile()) {
            throw new IllegalArgumentException("[JobX] jarFile " + jarFilePath + " is not file");
        }
        loadPath(jarFile.getAbsolutePath());
    }

    public static void loadJars(String path) {
        File jarDir = new File(path);
        if (!jarDir.exists()) {
            throw new IllegalArgumentException("[JobX] jarPath:" + path + " is not exists");
        }
        if (!jarDir.isDirectory()) {
            throw new IllegalArgumentException("[JobX] jarPath:" + path + " is not directory");
        }

        if (jarDir.listFiles().length == 0) {
            throw new IllegalArgumentException("[JobX] have not jar in path:" + path);
        }

        for (File jarFile : jarDir.listFiles()) {
            loadPath(jarFile.getAbsolutePath());
        }
    }

}