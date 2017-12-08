package org.opencron.server.bootstrap;


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
import org.opencron.common.utils.CommonUtils;
import org.opencron.common.utils.MavenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyLauncher implements Launcher {

    private Logger logger = LoggerFactory.getLogger(JettyLauncher.class);

    public void start(boolean devMode, int port) throws Exception {

        Server server = new Server(new QueuedThreadPool(500));

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
        if (CommonUtils.isWindowOs()) {
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
        connector.setHost("0.0.0.0");
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});
        server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 1024 * 1024 * 1024);
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);
        server.setHandler(appContext);
        logger.info("[opencron] JettyLauncher starting...");
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
