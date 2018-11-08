package com.jobxhub.service.api;


import com.jobxhub.service.model.User;

public interface UserService {

    User login(String userName, String password);

    boolean addUser(User model);

    boolean editPassword(Long id,String currPassword,String newPassword);
}
