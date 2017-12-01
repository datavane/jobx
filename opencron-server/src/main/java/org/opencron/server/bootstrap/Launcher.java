package org.opencron.server.bootstrap;


public interface Launcher {

    void start(boolean devMode, int port) throws Exception;

    void stop();

}
