package org.opencron.server.bootstrap;

import org.opencron.common.utils.ExtClasspathLoader;
import org.opencron.common.utils.MavenUtils;

import java.io.File;

public class Startup {

    public static void main(String[] args) {

        MavenUtils mavenUtils = MavenUtils.get(Startup.class.getClassLoader());

        String artifactName = mavenUtils.getArtifactId();

        String warName = artifactName.concat(".war");

        System.setProperty("catalina.home", "./".concat(artifactName));

        File warFile = new File("./".concat(artifactName).concat("/target/").concat(warName));
        if (!warFile.exists()) {
            throw new IllegalArgumentException("[opencron] start server error,please build project with maven first!");
        }

        //add jetty jar...
        String jettyJarPath = "./"+artifactName+"/target/jettylib";

        ExtClasspathLoader.loadJarByPath(jettyJarPath);

        JettyLauncher jettyLauncher = new JettyLauncher();
        jettyLauncher.start(warFile.getPath(),args);

    }

}

