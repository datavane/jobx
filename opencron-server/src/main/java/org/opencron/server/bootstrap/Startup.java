package org.opencron.server.bootstrap;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.opencron.common.utils.CommonUtils;
import org.opencron.common.utils.NetUtils;

import java.io.File;


public class Startup {

    private final static String warName = "opencron-server.war";

    private final static String artifactName = "opencron-server";

    private static int startPort = 8080;

    public static void main(String[] args) {

        System.setProperty("catalina.home","./".concat(artifactName));

        if ( CommonUtils.notEmpty(args) ) {
            Integer port = CommonUtils.toInt(args[0]);
            if (port == null || NetUtils.isInvalidPort(port)) {
                throw new IllegalArgumentException("[opencron] server port error: " + port );
            }
            startPort = port;
        }

        Server server = new Server(startPort);

        WebAppContext appContext = new WebAppContext();

        File warFile = new File("./".concat(artifactName).concat("/target/").concat(warName));
        //war存在
        if (warFile.exists()) {
            appContext.setWar(warFile.getAbsolutePath());
        }else {
            String baseDir = "./".concat(artifactName);
            appContext.setDescriptor(baseDir + "/src/main/webapp/WEB-INF/web.xml");
            appContext.setResourceBase(baseDir + "/src/main/webapp");
        }
        appContext.setThrowUnavailableOnStartupException(true);	// 在启动过程中允许抛出异常终止启动并退出 JVM
        appContext.setContextPath("/");
        appContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        appContext.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");

        server.setHandler(appContext);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}