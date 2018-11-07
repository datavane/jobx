package com.jobxhub.service.api;


import com.jobxhub.service.model.User;

public interface UserService {

    User login(String userName, String password);

    void addUser(User model);
}
