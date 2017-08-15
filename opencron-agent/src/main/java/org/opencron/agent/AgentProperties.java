/**
 * Copyright 2016 benjobs
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.opencron.agent;

import org.opencron.common.utils.LoggerFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;


/**
 * Utility class to read the bootstrap Opencron configuration.
 *
 * @author benjobs.
 */
public class AgentProperties {

    private static final Logger logger = LoggerFactory.getLogger(AgentProperties.class);

    private static Properties properties = null;

    /**
     * @param name The property name
     * @return specified property value
     */
    public static String getProperty(String name) {
        if ( properties == null ) {
            loadProperties();
        }
        return properties.getProperty(name);
    }

    /**
     * Load properties.
     */
    private static void loadProperties() {

        InputStream is = null;

        String fileName = "conf.properties";

        try {
            File home = new File(Configuration.OPENCRON_HOME);
            File conf = new File(home, "conf");
            File propsFile = new File(conf, fileName);
            is = new FileInputStream(propsFile);
        } catch (Throwable t) {
            handleThrowable(t);
        }

        if (is != null) {
            try {
                properties = new Properties();
                properties.load(is);
            } catch (Throwable t) {
                handleThrowable(t);
                logger.warn("[opencron] init properties error:{}",t.getMessage());
            } finally {
                try {
                    is.close();
                } catch (IOException ioe) {
                    logger.warn("[opencron]Could not close opencron properties file", ioe);
                }
            }
        }

        if (is == null) {
            // Do something
            logger.warn("[opencron]Failed to load opencron properties file");
            // That's fine - we have reasonable defaults.
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
        // All other instances of Throwable will be silently swallowed
    }


}
