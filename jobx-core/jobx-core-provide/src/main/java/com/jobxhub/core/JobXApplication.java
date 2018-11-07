package com.jobxhub.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication( exclude = DataSourceAutoConfiguration.class )
@MapperScan("com.jobxhub.core.dao")
public class JobXApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(JobXApplication.class).web(WebApplicationType.NONE).run(args);
    }
}