package com.jobxhub.server.util;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jobx", ignoreUnknownFields = true)
public class PropertyPlaceholder {
    private String registry;
    private String monitorPort;

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getMonitorPort() {
        return monitorPort;
    }

    public void setMonitorPort(String monitorPort) {
        this.monitorPort = monitorPort;
    }
}
