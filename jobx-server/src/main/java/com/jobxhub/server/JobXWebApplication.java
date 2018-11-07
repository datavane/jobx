package com.jobxhub.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;


@CrossOrigin//允许跨越访问
@SpringBootApplication(scanBasePackages = "com.jobxhub.server.controller")
public class JobXWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobXWebApplication.class,args);
    }

}