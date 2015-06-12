/**
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.inbound.endpoint.protocol.tcp.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Properties;

/**
 *
 */
public class TCPConfiguration {
    private static final Log log = LogFactory.getLog(TCPConfiguration.class);

    private static String CONF_LOCATION = "conf.location";
    private static TCPConfiguration instance = new TCPConfiguration();
    private Properties props;

    private TCPConfiguration(){
        try {
            props = loadProperties("tcp.properties");
            //log.info("so_timeout : "+getIntProperty(InboundTCPConstants.TCPConstants.SO_TIMEOUT));
        }catch (Exception e){
            log.info("could not load the properties from tcp.properties file...");
            //Customer can set manual location for configuration file. If it is not found we ignore it
        }
    }

    public static TCPConfiguration getInstance(){
        return instance;
    }

    /**
     * Get an int property that tunes TCP transport. Prefer system properties
     *
     * @param name name of the system/config property
     * @param def  default value to return if the property is not set
     * @return the value of the property to be used
     */

    public Integer getIntProperty(String name, Integer def) {
        String val = System.getProperty(name);
        if (val == null) {
            val = props.getProperty(name);
        }

        if (val != null) {
            int intVal;
            try {
                intVal = Integer.valueOf(val);
            } catch (NumberFormatException e) {
                log.warn("Invalid TCP tuning property value. " + name +
                         " must be an integer");
                return def;
            }
            if (log.isDebugEnabled()) {
                log.debug("Using TCP tuning parameter : " + name + " = " + val);
            }
            return intVal;
        }

        return def;
    }

    /**
     * Get an int property that tunes TCP transport. Prefer system properties
     *
     * @param name name of the system/config property
     * @return the value of the property, null if the property is not found
     */
    public Integer getIntProperty(String name) {
        return getIntProperty(name, null);
    }


    /**
     * Get a boolean property that tunes TCP transport. Prefer system properties
     *
     * @param name name of the system/config property
     * @param def  default value to return if the property is not set
     * @return the value of the property to be used
     */
    public Boolean getBooleanProperty(String name, Boolean def) {
        String val = System.getProperty(name);
        if (val == null) {
            val = props.getProperty(name);
        }

        if (val != null) {
            if (log.isDebugEnabled()) {
                log.debug("Using TCP tuning parameter : " + name + " = " + val);
            }
            return Boolean.valueOf(val);
        }

        return def;
    }

    /**
     * Get a Boolean property that tunes TCP transport. Prefer system properties
     *
     * @param name name of the system/config property
     * @return the value of the property, null if the property is not found
     */
    public Boolean getBooleanProperty(String name) {
        return getBooleanProperty(name, null);
    }

    /**
     * Get a String property that tunes TCP transport. Prefer system properties
     *
     * @param name name of the system/config property
     * @param def  default value to return if the property is not set
     * @return the value of the property to be used
     */
    public String getStringProperty(String name, String def) {
        String val = System.getProperty(name);
        if (val == null) {
            val = props.getProperty(name);
        }

        return val == null ? def : val;
    }


    /**
     * Loads the properties from a given property file path
     *
     * @param filePath Path of the property file
     * @return Properties loaded from given file
     */
    private static Properties loadProperties(String filePath) {

        Properties properties = new Properties();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        log.info("Loading the TCP config file '" + filePath + "' from classpath");
        if (log.isDebugEnabled()) {
            log.debug("Loading the TCP config file '" + filePath + "' from classpath");
        }

        InputStream in  = null;

        //if we reach to this assume that the we may have to looking to the customer provided external location for the
        //given properties

        if (System.getProperty(CONF_LOCATION) != null) {
            try {
                in = new FileInputStream(System.getProperty(CONF_LOCATION) + File.separator + filePath);
            } catch (FileNotFoundException e) {
                String msg = "Error loading TCP inbound properties from a file at from the System defined location: " +
                             filePath;
                log.warn(msg);
            }
        }


        if (in == null) {
            in = cl.getResourceAsStream(filePath);
            if (log.isDebugEnabled()) {
                log.debug("Unable to load file  '" + filePath + "'");
            }

            filePath = "conf" + File.separatorChar + filePath;
            if (log.isDebugEnabled()) {
                log.debug("Loading the file '" + filePath + "'");
            }

            in = cl.getResourceAsStream(filePath);
            if (in == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Unable to load file  '" + filePath + "'");
                }
            }
        }
        if (in != null) {
            try {
                properties.load(in);
            } catch (IOException e) {
                String msg = "Error loading properties from a file at : " + filePath;
                log.error(msg, e);
            }
        }
        return properties;
    }
}
