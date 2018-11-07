package com.jobxhub.server.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.jobxhub.core.api.UserService;
import com.jobxhub.core.model.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController  {

    @Reference(version = "${jobx.provide.version}",
            application = "${dubbo.application.id}",
            url = "dubbo://localhost:12345")
    private UserService userService;

    @PostMapping("login")
    public void login(String userName,String password){
        User user = userService.login(userName,password);
    }

}
