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

package com.jobxhub.server.bootstrap;

import static com.jobxhub.common.Constants.LauncherType;

import com.jobxhub.common.Constants;
import com.jobxhub.common.ext.ExtensionLoader;
import com.jobxhub.common.util.ClassLoaderUtils;
import com.jobxhub.common.util.IOUtils;
import com.jobxhub.common.util.MavenUtils;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Executors;

/**
 * @author benjobs
 */
public class Startup {

    private static final int MIN_PORT = 0;

    private static final int MAX_PORT = 65535;

    private static final String workspace = "container";

    private static int startPort = 20501;

    private static boolean devMode = true;

    public static void main(String[] args) {

        String portParam = System.getProperty("server.port");

        String launcher = System.getProperty("server.launcher");

        devMode = (launcher == null) ? true : false;

        LauncherType launcherType = (launcher == null || LauncherType.isTomcat(launcher)) ? LauncherType.TOMCAT : LauncherType.JETTY;

        URL bannerURL = Thread.currentThread().getContextClassLoader().getResource("app-banner.txt");
        if (bannerURL != null) {
            String banner = IOUtils.readText(new File(bannerURL.getPath()), Constants.CHARSET_UTF8);
            System.out.println(ansi().eraseScreen().fg(GREEN).a(banner).reset());
        }

        if (portParam == null) {
            System.out.printf("[JobX]Server At default port %d Starting...\n", startPort);
        } else {
            try {
                startPort = Integer.parseInt(portParam);
                if (startPort <= MIN_PORT || startPort > MAX_PORT) {
                    throw new IllegalArgumentException("[JobX] server port error: " + portParam);
                }
                System.out.printf("[JobX]server At port %d Starting...\n", startPort);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("[JobX] server port error: " + portParam);
            }
        }

        String jarPath;

        if (devMode) {
            String artifact = MavenUtils.get(Thread.currentThread().getContextClassLoader()).getArtifactId();
            jarPath = artifact + File.separator + workspace + File.separator + launcherType.getName();
            System.setProperty("catalina.home", artifact + File.separator + workspace);
        } else {
            jarPath = workspace + File.separator + launcherType.getName();
            System.setProperty("catalina.home", workspace);
        }

        //load jars.
        ClassLoaderUtils.loadJars(jarPath);

        final Launcher startLauncher = ExtensionLoader.load(Launcher.class, launcherType.getName());

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    startLauncher.start(devMode, startPort);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
