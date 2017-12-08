package org.opencron.server.bootstrap;

import org.opencron.common.utils.ExtClasspathLoader;
import org.opencron.common.utils.MavenUtils;

import java.io.File;

public class Startup {

    private static final int MIN_PORT = 0;

    private static final int MAX_PORT = 65535;

    private static int startPort = 8080;

    private static boolean devMode = true;

    private static final String workspace = "work";

    public static void main(String[] args) throws Exception {

        String portParam = System.getProperty("server.port");

        if (portParam == null ) {
            System.out.printf("[opencron]Server At default port %d Starting...", startPort);
        }else {
            try {
                startPort = Integer.parseInt(portParam);
                if (startPort <= MIN_PORT || startPort > MAX_PORT) {
                    throw new IllegalArgumentException("[opencron] server port error: " + portParam);
                }
                System.out.printf("[opencron]server At port %d Starting...", startPort);
            }catch (NumberFormatException e) {
                throw new IllegalArgumentException("[opencron] server port error: " + portParam);
            }
        }

        String launcher = System.getProperty("server.launcher");

        devMode =  ( launcher == null ) ? true : false;

        launcher = (launcher == null||launcher.trim().equals("tomcat")) ? "tomcat":"jetty";

        String jarPath;
        if (devMode) {
            String artifact = MavenUtils.get(Thread.currentThread().getContextClassLoader()).getArtifactId();
            jarPath = artifact + File.separator + workspace + File.separator + launcher;
            System.setProperty("catalina.home", artifact + File.separator + workspace);
        } else {
            jarPath = workspace + File.separator + launcher;
            System.setProperty("catalina.home", workspace);
        }
        //load jars.
        ExtClasspathLoader.scanJar(jarPath);

        if (launcher.equals("tomcat")) {
            TomcatLauncher tomcatLauncher = new TomcatLauncher();
            tomcatLauncher.start(devMode,startPort);
        }else {
            JettyLauncher jettyLauncher = new JettyLauncher();
            jettyLauncher.start(devMode,startPort);
        }

    }

}

