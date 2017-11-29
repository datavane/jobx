package org.opencron.server.bootstrap;

import org.opencron.common.utils.ExtClasspathLoader;
import org.opencron.common.utils.MavenUtils;

import java.io.File;

public class Startup {

    public static void main(String[] args) {

        String launcher = System.getProperty("server.launcher");

        String artifact = null;
        String jettyJarPath = null;
        File warFile = null;

        //dev 开发者模式通过ide启动的main
        if (launcher == null) {
            artifact = MavenUtils.get(Thread.currentThread().getContextClassLoader()).getArtifactId();
            jettyJarPath = "./".concat(artifact).concat("/jetty");
            warFile = new File( "./".concat(artifact).concat("/target/").concat(artifact).concat(".war") );
            System.setProperty("catalina.home","./".concat(artifact));
        }else if (launcher.equals("jetty")){
            //server.sh脚本启动的...
            jettyJarPath = "./jetty";
            System.setProperty("catalina.home","./");
        }
        ExtClasspathLoader.scanJar(jettyJarPath);
        JettyLauncher jettyLauncher = new JettyLauncher();
        jettyLauncher.start(artifact,warFile,launcher!=null,args);

    }


}

