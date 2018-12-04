/**
 * Copyright (c) 2015 The JobX Project
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jobxhub.server.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.jobxhub.common.Constants;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.DigestUtils;
import com.jobxhub.service.api.UserService;
import com.jobxhub.service.model.User;
import com.jobxhub.service.vo.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

import static com.jobxhub.common.util.WebUtils.getWebUrlPath;

@RestController
@RequestMapping("/passport")
@Slf4j
public class PassportController {

    @Reference
    private UserService userService;

    @PostMapping("/login")
    public RestResult login(HttpServletRequest request, HttpSession session, String userName, String password) throws IOException {

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

        String xsrf = (String) session.getAttribute(Constants.PARAM_XSRF_NAME_KEY);
        if (xsrf != null) {
            session.removeAttribute(xsrf);
            session.removeAttribute(Constants.PARAM_XSRF_NAME_KEY);
        }
        xsrf = CommonUtils.uuid();
        session.setAttribute(Constants.PARAM_XSRF_NAME_KEY, xsrf);
        //登陆成功了则生成csrf...
        log.info("[JobX]login seccussful,generate csrf:{}", xsrf);
        session.setAttribute(xsrf, user);
        return RestResult.ok().put("token", xsrf);
    }

}
