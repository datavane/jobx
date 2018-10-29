package com.jobxhub.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@ServletComponentScan
@MapperScan("com.jobxhub.server.dao")
@SpringBootApplication( exclude = DataSourceAutoConfiguration.class )
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}