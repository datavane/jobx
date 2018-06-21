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


import org.apache.tomcat.util.scan.StandardJarScanner;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import com.jobxhub.common.Constants;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.MavenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author benjobs
 */
public class JettyLauncher implements Launcher {

    private Logger logger = LoggerFactory.getLogger(JettyLauncher.class);

    @Override
    public void start(boolean devMode, int port) throws Exception {

        Server server = new Server(new QueuedThreadPool(Constants.WEB_THREADPOOL_SIZE));

        WebAppContext appContext = new WebAppContext();
        String resourceBasePath = "";
        //开发者模式
        if (devMode) {
            String artifact = MavenUtils.get(Thread.currentThread().getContextClassLoader()).getArtifactId();
            resourceBasePath = artifact + "/src/main/webapp";
        }
        appContext.setDescriptor(resourceBasePath + "WEB-INF/web.xml");
        appContext.setResourceBase(resourceBasePath);
        appContext.setExtractWAR(true);

        //init param
        appContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        if (CommonUtils.isWindows()) {
            appContext.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        }

        //for jsp support
        appContext.addBean(new JettyJspParser(appContext));
        appContext.addServlet(JettyJspServlet.class, "*.jsp");

        appContext.setContextPath("/");
        appContext.getServletContext().setExtendedListenerTypes(true);
        appContext.setParentLoaderPriority(true);
        appContext.setThrowUnavailableOnStartupException(true);
        appContext.setConfigurationDiscovered(true);
        appContext.setClassLoader(Thread.currentThread().getContextClassLoader());


        ServerConnector connector = new ServerConnector(server);
        connector.setHost("localhost");
        connector.setPort(port);

        server.setConnectors(new Connector[]{connector});
        server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 1024 * 1024 * 1024);
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);
        server.setHandler(appContext);
        logger.info("[JobX] JettyLauncher starting...");
        server.start();
    }

    @Override
    public void stop() {

    }

    private static class JettyJspParser extends AbstractLifeCycle implements ServletContextHandler.ServletContainerInitializerCaller {

        private JettyJasperInitializer jasperInitializer;
        private ServletContextHandler context;

        public JettyJspParser(ServletContextHandler context) {
            this.jasperInitializer = new JettyJasperInitializer();
            this.context = context;
            this.context.setAttribute("org.apache.tomcat.JarScanner", new StandardJarScanner());
        }

        @Override
        protected void doStart() throws Exception {
            Thread.currentThread().setContextClassLoader(context.getClassLoader());
            try {
                jasperInitializer.onStartup(null, context.getServletContext());
                super.doStart();
            } finally {
                Thread.currentThread().setContextClassLoader(Thread.currentThread().getContextClassLoader());
            }
        }

    }

}
