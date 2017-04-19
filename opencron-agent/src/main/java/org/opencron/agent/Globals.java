package org.opencron.agent;


import java.io.File;

public final class Globals {

    /**
     * Name of the system property containing
     */
    public static final String OPENCRON_HOME = "opencron.home";

    /**
     * port
     */
    public static String OPENCRON_PORT = System.getProperty("opencron.port");
    /**
     * password
     */
    public static String OPENCRON_PASSWORD = System.getProperty("opencron.password");

    /**
     * pid
     */
    public static File OPENCRON_PID_FILE = new File(System.getProperty("opencron.pid"));

    /**
     * password file
     */

    public static File OPENCRON_PASSWORD_FILE = new File(System.getProperty(OPENCRON_HOME) + File.separator + ".password");

    /**
     * monitor file
     */
    public static File OPENCRON_MONITOR_SHELL = new File(System.getProperty(OPENCRON_HOME) + "/bin/monitor.sh");

    /**
     * kill file
     */
    public static File OPENCRON_KILL_SHELL = new File(System.getProperty(OPENCRON_HOME) + "/bin/kill.sh");


    public static String OPENCRON_SOCKET_ADDRESS;

}
