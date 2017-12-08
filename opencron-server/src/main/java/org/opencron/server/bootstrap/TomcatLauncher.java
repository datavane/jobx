package org.opencron.server.bootstrap;


import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardThreadExecutor;
import org.apache.catalina.startup.Tomcat;
import org.opencron.common.utils.MavenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class TomcatLauncher implements Launcher {


    private static final String currentPath = "";

    private Logger logger = LoggerFactory.getLogger(TomcatLauncher.class);

    @Override
    public void start(boolean devMode,int port) throws Exception {

        String baseDir = currentPath;
        File webApp = new File(currentPath);

        if (devMode) {
            String artifact = MavenUtils.get(Thread.currentThread().getContextClassLoader()).getArtifactId();
            baseDir = artifact;
            webApp = new File(baseDir + "/src/main/webapp/");
        }

        Tomcat tomcat = new Tomcat();
        //host...
        tomcat.setPort(port);
        tomcat.getHost().setAppBase(currentPath);
        tomcat.setBaseDir(baseDir);
        tomcat.addWebapp(currentPath, webApp.getAbsolutePath());

        //init param
        StandardThreadExecutor executor = new StandardThreadExecutor();
        executor.setMaxThreads(500);
        //一旦出现问题便于查找问题,设置标识.
        executor.setNamePrefix("opencron-tomcat-");

        tomcat.getConnector().getService().addExecutor(executor);
        tomcat.getServer().addLifecycleListener(new LifecycleListener() {
            @Override
            public void lifecycleEvent(LifecycleEvent event) {
                if (event.getLifecycle().equals(Lifecycle.START_EVENT)) {
                    logger.info("[opencron] TomcatLauncher starting...");
                }
                if (event.getLifecycle().equals(Lifecycle.STOP_EVENT)) {
                    logger.info("[opencron] TomcatLauncher stopping...");
                }
            }
        });
        tomcat.start();
        tomcat.getServer().await();
    }

    @Override
    public void stop() {

    }
}
