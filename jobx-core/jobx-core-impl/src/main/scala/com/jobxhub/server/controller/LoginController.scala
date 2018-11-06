package com.jobxhub.core.controller

import java.io.{ByteArrayInputStream, File}

import com.jobxhub.common.Constants
import com.jobxhub.common.util.{DigestUtils, IOUtils}
import com.jobxhub.core.dto.{RestResult}
import com.jobxhub.core.service.UserService
import com.jobxhub.core.support.JobXTools
import javax.servlet.http.{HttpServletRequest, HttpServletResponse, HttpSession}
import org.slf4j.{LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PostMapping, ResponseBody}

import com.jobxhub.core.model.User

@ComponentScan
@Controller
@ResponseBody
class LoginController @Autowired()(private val userService: UserService) {

  private val logger = LoggerFactory.getLogger(getClass)

  @PostMapping(Array("/login.do"))
  @throws[Exception]
  def login(session: HttpSession, request: HttpServletRequest, response: HttpServletResponse, userName: String, password: String): RestResult = { //用户信息验证

    userService.login(request, userName, password) match {
      case 500 => RestResult.rest(500)
      case _ =>
        //登陆成功了则生成xsrf...
        val xsrf = JobXTools.generateXSRF(request, response)
        logger.info("[JobX]login seccussful,generate xsrf:{}", xsrf)

        val user = new User
        // JobXTools.getUser(session)
        //提示用户更改默认密码
        val salt = DigestUtils.decodeHex(user.salt)
        val hashPassword = DigestUtils.sha1(DigestUtils.md5Hex(Constants.PARAM_DEF_PASSWORD_KEY).toUpperCase.getBytes, salt, 1024)
        val hashPass = DigestUtils.encodeHex(hashPassword)

        user.userName match {
          case Constants.PARAM_DEF_USER_KEY if user.password == hashPass => RestResult.rest(201).put(Constants.PARAM_XSRF_NAME_KEY, xsrf)
          case _ =>
            if (user.headerPic != null) {
              val name = user.userId + "_140" + user.picExtName
              val path = request.getServletContext.getRealPath("/").replaceFirst("/$", "") + "/upload/" + name
              IOUtils.writeFile(new File(path), new ByteArrayInputStream(user.headerPic))
              // user.headerPic = (getWebUrlPath(request) + "/upload/" + name)
              session.setAttribute(Constants.PARAM_LOGIN_USER_KEY, user)
            }
            user.password = null
            user.salt = null
            RestResult.rest(200).put("user", user).put(Constants.PARAM_XSRF_NAME_KEY, xsrf)
        }
    }
  }

  @PostMapping(Array("logout.do"))
  @throws[Exception]
  def logout(request: HttpServletRequest): RestResult = {
    JobXTools.invalidSession(request)
    RestResult.rest(200)
  }
}
