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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyLauncher {

    private  Logger logger = LoggerFactory.getLogger(Startup.class);

    private static int startPort = 8080;

    public void start(String warPath,String[] args) {

        if (CommonUtils.notEmpty(args)) {
            Integer port = CommonUtils.toInt(args[0]);
            if (port == null || NetUtils.isInvalidPort(port)) {
                throw new IllegalArgumentException("[opencron] server port error: " + port);
            }
            startPort = port;
            logger.info("[opencron]Server At port {} Starting...", startPort);
        } else {
            logger.info("[opencron]Server At default port {} Starting...", startPort);
        }

        Server server = new Server(startPort);

        WebAppContext appContext = new WebAppContext();

        appContext.setWar(warPath);

        //init param
        appContext.setThrowUnavailableOnStartupException(true);    // 在启动过程中允许抛出异常终止启动并退出 JVM
        appContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        appContext.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");

        //for jsp support
        appContext.addBean(new JettyJspParser(appContext));
        appContext.addServlet(JettyJspServlet.class, "*.jsp");

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
