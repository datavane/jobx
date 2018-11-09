package com.jobxhub.service.api;


import com.jobxhub.service.model.User;
import com.jobxhub.service.vo.PageBean;

import java.io.File;
import java.util.List;

public interface UserService {

    User login(String userName, String password);

    boolean addUser(User model);

    boolean editPassword(Long id,String currPassword,String newPassword);

    boolean existsName(String name);

    List<String> getExecUser(Long userId);

    boolean uploadImg(Long userId, File file);

    boolean updateUser(User user);

    PageBean getPageBean(PageBean pageBean);
}
