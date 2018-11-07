package com.jobxhub.service;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.jobxhub.common.Constants;
import com.jobxhub.common.util.SystemPropertyUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication( exclude = DataSourceAutoConfiguration.class )
@MapperScan("com.jobxhub.service.dao")
@EnableDubbo
public class JobXApplication {

    public static void main(String[] args) {
        SystemPropertyUtils.getOrElseUpdate(Constants.PARAM_JOBX_HOME_KEY,".");
        new SpringApplicationBuilder(JobXApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

    }
}