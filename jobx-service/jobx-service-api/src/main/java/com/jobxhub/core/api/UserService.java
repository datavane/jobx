package com.jobxhub.core.api;


import com.jobxhub.core.entity.UserEntity;

public interface UserService {

    UserEntity login(String userName, String password);

    void addUser(UserEntity model);
}
