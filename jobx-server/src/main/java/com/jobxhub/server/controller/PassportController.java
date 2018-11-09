package com.jobxhub.server.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.jobxhub.common.Constants;
import com.jobxhub.common.util.DigestUtils;
import com.jobxhub.service.api.UserService;
import com.jobxhub.service.model.User;
import com.jobxhub.service.vo.RestResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

import static com.jobxhub.common.util.WebUtils.getWebUrlPath;

@RestController
@RequestMapping("/passport")
public class PassportController {

    @Reference
    private UserService userService;

    @PostMapping("/login")
    public RestResult login(HttpServletRequest request, String userName, String password) throws IOException {

        User user = userService.login(userName, password);

        if (user == null) {
            return RestResult.rest(500);
        }
        //提示用户更改默认密码
        byte[] salt = DigestUtils.decodeHex(user.getSalt());
        byte[] hashPassword = DigestUtils.sha1(DigestUtils.md5Hex(Constants.PARAM_DEF_PASSWORD_KEY).toUpperCase().getBytes(), salt, 1024);
        String hashPass = DigestUtils.encodeHex(hashPassword);

        if (user.getUserName().equals(Constants.PARAM_DEF_USER_KEY) && user.getPassword().equals(hashPass)) {
            return RestResult.rest(201);
        }

        if (user.getHeaderPic() != null) {
            String name = user.getUserId() + "_140" + user.getPicExtName();
            String path = request.getServletContext().getRealPath("/").replaceFirst("/$", "") + "/upload/" + name;
            File defImage = new File(path);
            userService.uploadImg(user.getUserId(), defImage);
            user.setHeaderPath(getWebUrlPath(request) + "/upload/" + name);
        }

        request.getSession().setAttribute(Constants.PARAM_LOGIN_USER_KEY, user);

        user.setPassword(null);
        user.setSalt(null);

        return RestResult.rest(200,user);

    }

}
