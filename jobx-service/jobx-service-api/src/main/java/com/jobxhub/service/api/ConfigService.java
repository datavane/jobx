package com.jobxhub.service.api;

import com.jobxhub.service.model.Config;

import java.sql.SQLException;
import java.util.List;

public interface ConfigService {

    Config getSysConfig();

    boolean update(Config config);

    void initDB() throws SQLException;

    List<String> getExecUser();
}
