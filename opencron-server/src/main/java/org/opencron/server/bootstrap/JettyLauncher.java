package org.opencron.server.bootstrap;


import org.apache.tomcat.util.scan.StandardJarScanner;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;
import org.opencron.common.utils.MavenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyLauncher implements Launcher {

    private  Logger logger = LoggerFactory.getLogger(Startup.class);

    public void start(boolean devMode,int port) {

        Server server = new Server(port);

        WebAppContext appContext = new WebAppContext();

        //开发者模式
        if (devMode) {
            String artifact = MavenUtils.get(Thread.currentThread().getContextClassLoader()).getArtifactId();
            String baseDir = "./".concat(artifact);
            appContext.setDescriptor(baseDir + "/src/main/webapp/WEB-INF/web.xml");
            appContext.setResourceBase(baseDir + "/src/main/webapp");
        }else {
            appContext.setDescriptor("./WEB-INF/web.xml");
            appContext.setResourceBase("./");
        }

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
            logger.info("[opencron] JettyLauncher starting...");
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
