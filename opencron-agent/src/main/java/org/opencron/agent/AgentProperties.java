/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencron.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;


/**
 * Utility class to read the bootstrap Opencron configuration.
 *
 * @author benjobs
 */
public class AgentProperties {

    private static final Logger log = LoggerFactory.getLogger(AgentProperties.class);

    private static Properties properties = null;

    static {
        loadProperties();
    }

    /**
     * @param name The property name
     * @return specified property value
     */
    public static String getProperty(String name) {
        return properties.getProperty(name);
    }

    public static Integer getInt(String name) {
        return Integer.parseInt(properties.getProperty(name));
    }

    public static Float getFloat(String name) {
        return Float.parseFloat(properties.getProperty(name));
    }

    public static Double getDouble(String name) {
        return Double.parseDouble(properties.getProperty(name));
    }

    /**
     * Load properties.
     */
    private static void loadProperties() {
        InputStream is = null;
        Throwable error = null;
        try {
            is = new FileInputStream(Globals.OPENCRON_CONF_FILE);
        } catch (Throwable t) {
            handleThrowable(t);
        }

        if (is != null) {
            try {
                properties = new Properties();
                properties.load(is);
            } catch (Throwable t) {
                handleThrowable(t);
                error = t;
            } finally {
                try {
                    is.close();
                } catch (IOException ioe) {
                    log.warn("Could not close opencron.properties", ioe);
                }
            }
        }

        if ((is == null) || (error != null)) {
            // Do something
            log.warn("Failed to load opencron.properties", error);
            properties = new Properties();
        }

        // Register the properties as system properties
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String name = (String) enumeration.nextElement();
            String value = properties.getProperty(name);
            if (value != null) {
                System.setProperty(name, value);
            }
        }
    }


    // Copied from ExceptionUtils since that class is not visible during start
    private static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath) t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError) t;
        }
    }
}
