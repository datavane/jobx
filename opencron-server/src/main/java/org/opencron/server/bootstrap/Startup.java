package org.opencron.server.bootstrap;


import org.apache.tomcat.util.scan.StandardJarScanner;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;
import org.opencron.common.utils.CommonUtils;
import org.opencron.common.utils.NetUtils;

import java.io.File;

public class Startup {

    private final static String warName = "opencron-server.war";

    private final static String artifactName = "opencron-server";

    private static int startPort = 8090;

    public static void main(String[] args) {

        System.setProperty("catalina.home","./".concat(artifactName));

        if ( CommonUtils.notEmpty(args) ) {
            Integer port = CommonUtils.toInt(args[0]);
            if (port == null || NetUtils.isInvalidPort(port)) {
                throw new IllegalArgumentException("[opencron] server port error: " + port );
            }
            startPort = port;
            System.out.printf("[opencron]Server At port %d Starting...",startPort);
        }else {
            System.out.printf("[opencron]Server At default port %d Starting...",startPort);
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

        //for jsp support
        appContext.addBean(new JspStarter(appContext));
        appContext.addServlet(JettyJspServlet.class, "*.jsp");

        //init param
        appContext.setThrowUnavailableOnStartupException(true);	// 在启动过程中允许抛出异常终止启动并退出 JVM
        appContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        appContext.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");

        appContext.setContextPath("/");
        appContext.setParentLoaderPriority(true);
        appContext.setClassLoader(Thread.currentThread().getContextClassLoader());

        server.setStopAtShutdown(true);
        server.setHandler(appContext);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class JspStarter extends AbstractLifeCycle implements ServletContextHandler.ServletContainerInitializerCaller {

        JettyJasperInitializer jasperInitializer;
        ServletContextHandler context;

        public JspStarter(ServletContextHandler context) {
            this.jasperInitializer = new JettyJasperInitializer();
            this.context = context;
            this.context.setAttribute("org.apache.tomcat.JarScanner", new StandardJarScanner());
        }

        @Override
        protected void doStart() throws Exception {
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(context.getClassLoader());
            try {
                jasperInitializer.onStartup(null, context.getServletContext());
                super.doStart();
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        }
    }

}