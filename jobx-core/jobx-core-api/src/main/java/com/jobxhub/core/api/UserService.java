package com.jobxhub.core.api;


import com.jobxhub.core.entity.UserEntity;
import com.jobxhub.core.model.User;

public interface UserService {

    User login(String userName, String password);

    void addUser(UserEntity model);
}
