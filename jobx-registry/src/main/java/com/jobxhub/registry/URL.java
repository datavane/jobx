/**
 * Copyright (c) 2015 The JobX Project
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

package com.jobxhub.registry;

import com.jobxhub.common.Constants;
import com.jobxhub.common.util.collection.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import com.jobxhub.common.util.collection.HashMap;

public final class URL {

    private final String protocol;

    private final String username;

    private final String password;

    private final String host;

    private final int port;

    private final String path;

    private final Map<String, String> parameters;

    private volatile transient Map<String, Number> numbers;

    private volatile transient Map<String, URL> urls;

    protected URL() {
        this.protocol = null;
        this.username = null;
        this.password = null;
        this.host = null;
        this.port = 0;
        this.path = null;
        this.parameters = null;
    }

    public URL(String protocol, String host, int port) {
        this(protocol, null, null, host, port, null, (Map<String, String>) null);
    }

    public URL(String protocol, String host, int port, String[] pairs) { // 变长参数...与下面的path参数冲突，改为数组
        this(protocol, null, null, host, port, null, CollectionUtils.toStringMap(pairs));
    }

    public URL(String protocol, String host, int port, Map<String, String> parameters) {
        this(protocol, null, null, host, port, null, parameters);
    }

    public URL(String protocol, String host, int port, String path) {
        this(protocol, null, null, host, port, path, (Map<String, String>) null);
    }

    public URL(String protocol, String host, int port, String path, String... pairs) {
        this(protocol, null, null, host, port, path, CollectionUtils.toStringMap(pairs));
    }

    public URL(String protocol, String host, int port, String path, Map<String, String> parameters) {
        this(protocol, null, null, host, port, path, parameters);
    }

    public URL(String protocol, String username, String password, String host, int port, String path) {
        this(protocol, username, password, host, port, path, (Map<String, String>) null);
    }

    public URL(String protocol, String username, String password, String host, int port, String path, String... pairs) {
        this(protocol, username, password, host, port, path, CollectionUtils.toStringMap(pairs));
    }

    public URL(String protocol, String username, String password, String host, int port, String path, Map<String, String> parameters) {
        if ((username == null || username.length() == 0)
                && password != null && password.length() > 0) {
            throw new IllegalArgumentException("Invalid url, password without username!");
        }
        this.protocol = protocol;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = (port < 0 ? 0 : port);
        // trim the beginning "/"
        while (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        this.path = path;
        if (parameters == null) {
            parameters = new HashMap<String, String>();
        } else {
            parameters = new HashMap<String, String>(parameters);
        }
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    /**
     * Parse url string
     *
     * @param url URL string
     * @return URL instance
     * @see URL
     */
    public static URL valueOf(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            throw new IllegalArgumentException("url == null");
        }
        String protocol = null;
        String username = null;
        String password = null;
        String host = null;
        int port = 0;
        String path = null;
        Map<String, String> parameters = null;
        int i = url.indexOf("?"); // seperator between body and parameters
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("\\&");
            parameters = new HashMap<String, String>();
            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int j = part.indexOf('=');
                    if (j >= 0) {
                        parameters.put(part.substring(0, j), part.substring(j + 1));
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://");
        if (i >= 0) {
            if (i == 0) throw new IllegalStateException("url missing protocol: \"" + url + "\"");
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        } else {
            // case: file:/path/to/file.txt
            i = url.indexOf(":/");
            if (i >= 0) {
                if (i == 0) throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                protocol = url.substring(0, i);
                url = url.substring(i + 1);
            }
        }

        i = url.indexOf("/");
        if (i >= 0) {
            path = url.substring(i + 1);
            url = url.substring(0, i);
        }
        i = url.indexOf("@");
        if (i >= 0) {
            username = url.substring(0, i);
            int j = username.indexOf(":");
            if (j >= 0) {
                password = username.substring(j + 1);
                username = username.substring(0, j);
            }
            url = url.substring(i + 1);
        }
        i = url.indexOf(":");
        if (i >= 0 && i < url.length() - 1) {
            port = Integer.parseInt(url.substring(i + 1));
            url = url.substring(0, i);
        }
        if (url.length() > 0) host = url;
        return new URL(protocol, username, password, host, port, path, parameters);
    }

    public static String encode(String value) {
        if (value == null || value.length() == 0) {
            return "";
        }
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String decode(String value) {
        if (value == null || value.length() == 0) {
            return "";
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public String getBackupAddress() {
        return getBackupAddress(0);
    }

    public String getBackupAddress(int defaultPort) {
        StringBuilder address = new StringBuilder(appendDefaultPort(getAddress(), defaultPort));
        String[] backups = getParameter(Constants.PARAM_BACKUP_KEY, new String[0]);
        if (backups != null && backups.length > 0) {
            for (String backup : backups) {
                address.append(",");
                address.append(appendDefaultPort(backup, defaultPort));
            }
        }
        return address.toString();
    }

    public List<URL> getBackupUrls() {
        List<URL> urls = new ArrayList<URL>();
        urls.add(this);
        String[] backups = getParameter(Constants.PARAM_BACKUP_KEY, new String[0]);
        if (backups != null && backups.length > 0) {
            for (String backup : backups) {
                urls.add(this.setAddress(backup));
            }
        }
        return urls;
    }


    public String getProtocol() {
        return protocol;
    }

    public URL setProtocol(String protocol) {
        return new URL(protocol, username, password, host, port, path, getParameters());
    }

    public String getUsername() {
        return username;
    }

    public URL setUsername(String username) {
        return new URL(protocol, username, password, host, port, path, getParameters());
    }

    public String getPassword() {
        return password;
    }

    public URL setPassword(String password) {
        return new URL(protocol, username, password, host, port, path, getParameters());
    }

    public String getAuthority() {
        if ((username == null || username.length() == 0)
                && (password == null || password.length() == 0)) {
            return null;
        }
        return (username == null ? "" : username)
                + ":" + (password == null ? "" : password);
    }

    public String getHost() {
        return host;
    }

    public URL setHost(String host) {
        return new URL(protocol, username, password, host, port, path, getParameters());
    }

    public int getPort() {
        return port;
    }

    public URL setPort(int port) {
        return new URL(protocol, username, password, host, port, path, getParameters());
    }

    public int getPort(int defaultPort) {
        return port <= 0 ? defaultPort : port;
    }

    public String getAddress() {
        return port <= 0 ? host : host + ":" + port;
    }

    public URL setAddress(String address) {
        int i = address.lastIndexOf(':');
        String host;
        int port = this.port;
        if (i >= 0) {
            host = address.substring(0, i);
            port = Integer.parseInt(address.substring(i + 1));
        } else {
            host = address;
        }
        return new URL(protocol, username, password, host, port, path, getParameters());
    }

    private String appendDefaultPort(String address, int defaultPort) {
        if (address != null && address.length() > 0
                && defaultPort > 0) {
            int i = address.indexOf(':');
            if (i < 0) {
                return address + ":" + defaultPort;
            } else if (Integer.parseInt(address.substring(i + 1)) == 0) {
                return address.substring(0, i + 1) + defaultPort;
            }
        }
        return address;
    }

    public String getPath() {
        return path;
    }

    public URL setPath(String path) {
        return new URL(protocol, username, password, host, port, path, getParameters());
    }

    public String getAbsolutePath() {
        if (path != null && !path.startsWith("/")) {
            return "/" + path;
        }
        return path;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameterAndDecoded(String key) {
        return getParameterAndDecoded(key, null);
    }

    public String getParameterAndDecoded(String key, String defaultValue) {
        return decode(getParameter(key, defaultValue));
    }

    public String getParameter(String key) {
        String value = parameters.get(key);
        if (value == null || value.length() == 0) {
            value = parameters.get(Constants.DEFAULT_KEY_PREFIX + key);
        }
        return value;
    }

    public String getParameter(String key, String defaultValue) {
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return value;
    }

    public String[] getParameter(String key, String[] defaultValue) {
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return Constants.COMMA_SPLIT_PATTERN.split(value);
    }

    private Map<String, Number> getNumbers() {
        if (numbers == null) { // 允许并发重复创建
            numbers = new HashMap<String, Number>();
        }
        return numbers;
    }

    private Map<String, URL> getUrls() {
        if (urls == null) { // 允许并发重复创建
            urls = new HashMap<String, URL>();
        }
        return urls;
    }

    public boolean isAnyHost() {
        return Constants.PARAM_ANYHOST_VALUE.equals(host) || getParameter(Constants.PARAM_ANYHOST_KEY, false);
    }

    public URL getUrlParameter(String key) {
        URL u = getUrls().get(key);
        if (u != null) {
            return u;
        }
        String value = getParameterAndDecoded(key);
        if (value == null || value.length() == 0) {
            return null;
        }
        u = URL.valueOf(value);
        getUrls().put(key, u);
        return u;
    }

    public double getParameter(String key, double defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.doubleValue();
        }
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        double d = Double.parseDouble(value);
        getNumbers().put(key, d);
        return d;
    }

    public float getParameter(String key, float defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.floatValue();
        }
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        float f = Float.parseFloat(value);
        getNumbers().put(key, f);
        return f;
    }

    public long getParameter(String key, long defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.longValue();
        }
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        long l = Long.parseLong(value);
        getNumbers().put(key, l);
        return l;
    }

    public int getParameter(String key, int defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.intValue();
        }
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        int i = Integer.parseInt(value);
        getNumbers().put(key, i);
        return i;
    }

    public short getParameter(String key, short defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.shortValue();
        }
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        short s = Short.parseShort(value);
        getNumbers().put(key, s);
        return s;
    }

    public byte getParameter(String key, byte defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.byteValue();
        }
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        byte b = Byte.parseByte(value);
        getNumbers().put(key, b);
        return b;
    }

    public char getParameter(String key, char defaultValue) {
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return value.charAt(0);
    }

    public boolean getParameter(String key, boolean defaultValue) {
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    public boolean hasParameter(String key) {
        String value = getParameter(key);
        return value != null && value.length() > 0;
    }

    private void buildParameters(StringBuilder buf, boolean concat, String[] parameters) {
        if (getParameters() != null && getParameters().size() > 0) {
            List<String> includes = (parameters == null || parameters.length == 0 ? null : Arrays.asList(parameters));
            boolean first = true;
            for (Map.Entry<String, String> entry : new TreeMap<String, String>(getParameters()).entrySet()) {
                if (entry.getKey() != null && entry.getKey().length() > 0
                        && (includes == null || includes.contains(entry.getKey()))) {
                    if (first) {
                        if (concat) {
                            buf.append("?");
                        }
                        first = false;
                    } else {
                        buf.append("&");
                    }
                    buf.append(entry.getKey());
                    buf.append("=");
                    buf.append(entry.getValue() == null ? "" : entry.getValue().trim());
                }
            }
        }
    }

    public java.net.URL toJavaURL() {
        try {
            return new java.net.URL(toString());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public InetSocketAddress toInetSocketAddress() {
        return new InetSocketAddress(host, port);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + port;
        result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        URL other = (URL) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (parameters == null) {
            if (other.parameters != null)
                return false;
        } else if (!parameters.equals(other.parameters))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (port != other.port)
            return false;
        if (protocol == null) {
            if (other.protocol != null)
                return false;
        } else if (!protocol.equals(other.protocol))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

}
