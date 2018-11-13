package com.jobxhub.service;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.jobxhub.common.Constants;
import com.jobxhub.common.util.DateUtils;
import com.jobxhub.common.util.SystemPropertyUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Date;

@EnableTransactionManagement
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@MapperScan("com.jobxhub.service.dao")
@EnableDubbo
public class JobXServiceApplication {

    public static void main(String[] args) {

        SystemPropertyUtils.getOrElseUpdate(Constants.PARAM_JOBX_HOME_KEY, ".");

        //register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger logger = LoggerFactory.getLogger(JobXServiceApplication.class);
            logger.info("[JobX] run shutdown hook {}", DateUtils.formatFullDate(new Date()));
        }, "JobXServiceShutdownHook"));

        new SpringApplicationBuilder(JobXServiceApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

    }

}