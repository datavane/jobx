package com.jobxhub.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@ServletComponentScan
@SpringBootApplication( exclude = DataSourceAutoConfiguration.class )
@MapperScan("com.jobxhub.server.dao")
public class JobXServer {

    public static void main(String[] args) {
        SpringApplication.run(JobXServer.class, args);
    }

}