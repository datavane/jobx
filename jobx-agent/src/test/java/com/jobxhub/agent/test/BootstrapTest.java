package com.jobxhub.agent.test;

import com.jobxhub.agent.service.AgentService;
import com.jobxhub.common.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import com.jobxhub.common.ext.ExtensionLoader;
import com.jobxhub.common.logging.LoggerFactory;
import com.jobxhub.common.util.SystemPropertyUtils;
import com.jobxhub.rpc.Server;
import org.slf4j.Logger;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;

public class BootstrapTest implements Serializable {

    private static final long serialVersionUID = 20150614L;

    private static final Logger logger = LoggerFactory.getLogger(SystemPropertyUtils.class);

    /**
     * thrift server
     */
    private Server server;

    /**
     * bootstrap instance....
     */
    private static BootstrapTest daemon;

    public static void main(String[] args) {

        if (daemon == null) {
            daemon = new BootstrapTest();
        }

        try {
            daemon.start();
            Thread.sleep(Integer.MAX_VALUE);
        } catch (Throwable t) {
            if (t instanceof InvocationTargetException && t.getCause() != null) {
                t = t.getCause();
            }
            handleThrowable(t);
            t.printStackTrace();
            System.exit(1);
        }
    }

    private void start() throws Exception {
        try {
            final int port = 1577;
            String password = DigestUtils.md5Hex("jobx").toLowerCase();
            System.setProperty(Constants.PARAM_JOBX_PORT_KEY, port + "");
            System.setProperty(Constants.PARAM_JOBX_PASSWORD_KEY, password);
            System.setProperty(Constants.PARAM_JOBX_HOST_KEY, "127.0.0.1");
            System.setProperty(Constants.PARAM_JOBX_HOME_KEY, "jobx-agent");
            System.setProperty("java.io.tmpdir", "tmp");
            System.setProperty(Constants.PARAM_JOBX_REGISTRY_KEY, "zookeeper://127.0.0.1:2181");

            this.server = ExtensionLoader.load(Server.class);
            //new thread to start for netty server
            new Thread(new Runnable() {
                @Override
                public void run() {
                    server.start(port, new AgentService());
                }
            }).start();

            logger.info("[JobX]agent started @ port:{},pid:{}", port, getPid());
            Thread.sleep(5000);
            AgentService.register(System.getProperty(Constants.PARAM_JOBX_HOST_KEY),port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath) t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError) t;
        }
    }

    private static Integer getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName();
        try {
            return Integer.parseInt(name.substring(0, name.indexOf('@')));
        } catch (Exception e) {
        }
        return -1;
    }

}