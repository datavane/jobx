package com.jobxhub.server.controller;

import com.jobxhub.common.Constants;
import com.jobxhub.common.job.Response;
import com.jobxhub.common.util.DigestUtils;
import com.jobxhub.common.util.IOUtils;
import com.jobxhub.common.util.collection.ParamsMap;
import com.jobxhub.server.annotation.RequestRepeat;
import com.jobxhub.server.dto.RestResult;
import com.jobxhub.server.dto.User;
import com.jobxhub.server.service.UserService;
import com.jobxhub.server.support.JobXTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.File;

import static com.jobxhub.common.util.WebUtils.getWebUrlPath;

@RestController
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    public RestResult login(HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestParam String userName, @RequestParam String password) throws Exception {
        //用户信息验证
        int status = userService.login(request, userName, password);

        if (status == 500) {
            return RestResult.rest(500);
        }
        //登陆成功了则生成xsrf...
        String xsrf = JobXTools.generateXSRF(request, response);
        logger.info("[JobX]login seccussful,generate xsrf:{}", xsrf);

        User user = JobXTools.getUser(session);
        //提示用户更改默认密码
        byte[] salt = DigestUtils.decodeHex(user.getSalt());
        byte[] hashPassword = DigestUtils.sha1(DigestUtils.md5Hex(Constants.PARAM_DEF_PASSWORD_KEY).toUpperCase().getBytes(), salt, 1024);
        String hashPass = DigestUtils.encodeHex(hashPassword);

        if (user.getUserName().equals(Constants.PARAM_DEF_USER_KEY) && user.getPassword().equals(hashPass)) {
            return RestResult.rest(201).put(Constants.PARAM_XSRF_NAME_KEY,xsrf);
        }

        if (user.getHeaderPic() != null) {
            String name = user.getUserId() + "_140" + user.getPicExtName();
            String path = request.getServletContext().getRealPath("/").replaceFirst("/$", "") + "/upload/" + name;
            IOUtils.writeFile(new File(path), new ByteArrayInputStream(user.getHeaderPic()));
            user.setHeaderPath(getWebUrlPath(request) + "/upload/" + name);
            session.setAttribute(Constants.PARAM_LOGIN_USER_KEY, user);
        }
        user.setPassword(null);
        user.setSalt(null);
        return RestResult.rest(200).put("user",user).put(Constants.PARAM_XSRF_NAME_KEY,xsrf);
    }


    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    public RestResult logout(HttpServletRequest request) throws Exception {
        JobXTools.invalidSession(request);
        return RestResult.rest(200);
    }
}
