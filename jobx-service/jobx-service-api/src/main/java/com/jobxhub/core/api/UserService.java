package com.jobxhub.core.api;


import com.jobxhub.core.model.UserModel;

public interface UserService {

    UserModel login(String userName, String password);

    void addUser(UserModel model);
}
