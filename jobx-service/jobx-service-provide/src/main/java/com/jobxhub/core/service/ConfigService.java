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

package com.jobxhub.core.service;

import com.jobxhub.common.Constants;
import com.jobxhub.common.util.*;
import com.jobxhub.common.util.collection.HashMap;
import com.jobxhub.core.dao.ConfigDao;
import com.jobxhub.core.model.Config;
import com.jobxhub.core.model.UserAgent;
import com.jobxhub.core.entity.ConfigEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.*;

/**
 * Created by ChenHui on 2016/2/17.
 */
@Service
public class ConfigService {

    private final Logger logger = LoggerFactory.getLogger(ConfigService.class);

    @Autowired
    private ConfigDao configDao;

    @Autowired
    private DataSource druidDataSource;

    @Autowired
    private UserAgentService userAgentService;

    private Connection connection;

    private String updateSQLFormat = "sql/%s-%s.sql";

    public Config getSysConfig() {
        List<ConfigEntity> configList = configDao.getConfig();
        if (CommonUtils.notEmpty(configList)) {
            Config config = new Config();
            for (ConfigEntity configEntity:configList) {
                config.fromEntity(configEntity);
            }
            return config;
        }
        return null;
    }

    public void update(Config config) {
        List<ConfigEntity> configEntity = Config.toEntity(config);
        for (ConfigEntity bean:configEntity) {
            configDao.update(bean);
        }
    }

    public void initDB() throws SQLException {
        connection = druidDataSource.getConnection();
        Connection connection = druidDataSource.getConnection();
        connection.setAutoCommit(true);
        try {
            boolean tableExist = validateTableExist(connection);
            //表存在...
            if (tableExist) {
                //检查升级更新...
                updateTable();
            } else {
                //不存在,创建新表,初始化数据...
                createTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //skip.....
        } finally {
            connection.close();
        }
    }

    private void updateTable() throws Exception {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from t_config limit 1");
        ResultSetMetaData metaData = resultSet.getMetaData();
        boolean version = false;
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            // resultSet数据下标从1开始
            String columnName = metaData.getColumnName(i + 1);
            if (columnName.equals("config_key")) {
                version = true;
                break;
            }
        }

        if (resultSet!=null) {
            resultSet.close();
        }

        //t_config表中有version字段
        String oldVersion = "V1.1.0";
        if (version) {
            oldVersion = getSysConfig().getVersion();
        }

        //无需升级版本
        if (oldVersion.equalsIgnoreCase(Constants.JOBX_VERSION)) {
            return;
        }

        String sqlFile = String.format(updateSQLFormat, oldVersion, Constants.JOBX_VERSION);
        URL url = Thread.currentThread().getContextClassLoader().getResource(sqlFile);
        if (url == null) {
            logger.warn("[JobX] version {} up to {} update'sql not found...", oldVersion, Constants.JOBX_VERSION);
            return;
        }

        String content = IOUtils.readText(new File(url.getFile()), Constants.CHARSET_UTF8);

        List<Map<String,String>> sqlList = parseSQL(content);

        for (Map<String,String> map:sqlList) {
            if (CommonUtils.notEmpty(map) && map.size() == 1) {
                Map.Entry<String, String> entry = map.entrySet().iterator().next();
                try {
                    logger.info("[JobX] update sql[{}] Starting...", entry.getValue());
                    statement.executeUpdate(entry.getKey());
                } catch (Exception e) {
                    logger.error("[JobX] update sql[{}] error:{}", entry.getValue(), e.getMessage());
                }
            }
        }

        updateVersionV110ToV120(oldVersion,statement);

        statement.close();
    }

    //V1.1.0升级V1.2.0特殊操作....
    private void updateVersionV110ToV120(String oldVersion,Statement statement ) throws SQLException {
        if ( oldVersion.equalsIgnoreCase("V1.1.0") &&
                Constants.JOBX_VERSION.equalsIgnoreCase("V1.2.0")) {
            logger.info("[JobX] V1.1.0 upto V1.2.0  processing the agentIds field in the t_user table...");
            String sql = "select user_id,agentIds from t_user";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Long userId = rs.getLong(1);
                String agentIds = rs.getString(2);
                if (CommonUtils.notEmpty(agentIds)) {
                    String[] idArray = agentIds.split(",");
                    for (String id:idArray) {
                        Long agentId = Long.parseLong(id);
                        UserAgent userAgent = new UserAgent(userId,agentId);
                        userAgentService.save(userAgent);
                    }
                }
            }
            logger.info("[JobX] V1.1.0 upto V1.2.0  drop column agentIds from t_user...");
            sql = "alter table t_user drop column agentIds";
            statement.executeUpdate(sql);
        }
    }

    private void createTable() throws SQLException {
        String sqlFile = "sql/" + Constants.JOBX_VERSION + ".sql";
        URL url = Thread.currentThread().getContextClassLoader().getResource(sqlFile);
        if (url == null) {
            throw new ExceptionInInitializerError("[JobX] sqlFile:" + sqlFile + " not found...");
        }
        String content = IOUtils.readText(new File(url.getFile()), Constants.CHARSET_UTF8);
        List<Map<String,String>> sqlList = parseSQL(content);
        String separator = StringUtils.line("-", 100);
        Statement statement = connection.createStatement();
        for (Map<String,String> map:sqlList) {
            if (CommonUtils.notEmpty(map) && map.size() == 1) {
                Map.Entry<String, String> entry = map.entrySet().iterator().next();
                try {
                    logger.info("[JobX] create table Starting...{}\n{}", entry.getValue(), separator);
                    statement.executeUpdate(entry.getKey());
                } catch (Exception e) {
                    logger.error("[JobX] create table Error:sql:{},info:{}", entry.getValue(), e.getMessage());
                }
            }
        }
        statement.close();
    }

    private List<Map<String,String>> parseSQL(String content) {
        String[] sqlArray = content.split("\\;");
        List<Map<String,String>> sqlList = new ArrayList<Map<String, String>>(0);
        for (String sql : sqlArray) {
            if (CommonUtils.notEmpty(sql)) {
                String script = sql.replaceAll("--(.*)\\n", "").replaceAll("\\n|\\r", " ").trim();
                Map<String, String> sqlMap = new HashMap<String, String>(0);
                sqlMap.put(script, sql.replaceAll("^((\\r\\n)|\\n)",""));
                sqlList.add(sqlMap);
            }
        }
        return sqlList;
    }

    public boolean validateTableExist(Connection conn) {
        boolean flag = true;
        String sql = "select count(1) from t_config";
        //获取连接
        try {
            Statement statement = conn.createStatement();
            statement.executeQuery(sql);
            statement.close();
        } catch (Exception e) {
            if (e.getLocalizedMessage().contains("doesn't exist")) {
                flag = false;
            }
        }
        return flag;
    }

    public List<String> getExecUser() {
        String execUser = configDao.getExecUser();
        if (CommonUtils.notEmpty(execUser)) {
            return Arrays.asList(execUser.split(","));
        }
        return Collections.emptyList();
    }
}
