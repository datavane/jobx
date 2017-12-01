package org.opencron.server.bootstrap;


import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;
import org.opencron.common.utils.MavenUtils;

import java.io.File;

public class TomcatLauncher implements Launcher {

    @Override
    public void start(boolean devMode,int port) throws Exception {

        //get webapp path...
        File webApp = new File("./");
        String baseDir = ".";
        if (devMode) {
            String artifact = MavenUtils.get(Thread.currentThread().getContextClassLoader()).getArtifactId();
            baseDir = "./".concat(artifact);
            String webAppPath = baseDir.concat("/src/main/webapp/");
            webApp = new File(webAppPath);
        }

        //appBase
        String appBase = System.getProperty("user.dir") + File.separator + ".";

        Tomcat tomcat = new Tomcat();

        tomcat.setHostname("localhost");
        tomcat.setPort(port);
        tomcat.setBaseDir(baseDir);

        StandardServer server = (StandardServer) tomcat.getServer();
        AprLifecycleListener listener = new AprLifecycleListener();
        server.addLifecycleListener(listener);
        tomcat.getHost().setAppBase(appBase);
        tomcat.addWebapp("", webApp.getAbsolutePath());

        tomcat.start();
        tomcat.getServer().await();
    }

    @Override
    public void stop() {

    }
}
