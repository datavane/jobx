/**
 * Copyright 2016 benjobs
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

package org.opencron.server.controller;

import com.alibaba.fastjson.JSON;
import org.opencron.common.job.Opencron;
import org.opencron.common.job.Response;
import org.opencron.common.utils.*;
import org.opencron.server.domain.Agent;
import org.opencron.server.domain.Job;
import org.opencron.server.domain.User;
import org.opencron.server.job.OpencronTools;
import org.opencron.server.service.*;
import org.opencron.server.tag.PageBean;
import org.opencron.server.vo.ChartVo;
import org.opencron.server.vo.Cropper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.File;
import java.util.Date;
import java.util.List;

import static org.opencron.common.utils.CommonUtils.isEmpty;
import static org.opencron.common.utils.CommonUtils.notEmpty;

/**
 * Created by ChenHui on 2016/2/17.
 */
@Controller
public class HomeController  extends BaseController{

    @Autowired
    private HomeService homeService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ExecuteService executeService;

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;


    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("/home")
    public String index(HttpSession session,Model model) {

        /**
         * agent...
         */
        List<Agent> success = agentService.getOwnerAgentByStatus(session,1);
        List<Agent> failed = agentService.getOwnerAgentByStatus(session,0);
        model.addAttribute("success", success.size());
        model.addAttribute("failed", failed.size());

        success.addAll(failed);
        model.addAttribute("agents", success);

        /**
         * job
         */
        List<Job> singleton = jobService.getJobsByJobType(session,Opencron.JobType.SINGLETON);
        List<Job> flow = jobService.getJobsByJobType(session,Opencron.JobType.FLOW);

        model.addAttribute("singleton", singleton.size());
        model.addAttribute("flow", flow.size());
        model.addAttribute("job", singleton.size() + flow.size());

        /**
         * 成功作业,自动执行
         */
        Long successAutoRecord = recordService.getRecords(session,1, Opencron.ExecType.AUTO);
        Long successOperRecord = recordService.getRecords(session,1, Opencron.ExecType.OPERATOR);

        model.addAttribute("successAutoRecord", successAutoRecord);
        model.addAttribute("successOperRecord", successOperRecord);
        model.addAttribute("successRecord", successAutoRecord + successOperRecord);

        /**
         * 失败作业
         */
        Long failedAutoRecord = recordService.getRecords(session,0, Opencron.ExecType.AUTO);
        Long failedOperRecord = recordService.getRecords(session,0, Opencron.ExecType.OPERATOR);
        model.addAttribute("failedAutoRecord", failedAutoRecord);
        model.addAttribute("failedOperRecord", failedOperRecord);
        model.addAttribute("failedRecord", failedAutoRecord + failedOperRecord);

        model.addAttribute("startTime", DateUtils.getCurrDayPrevDay(7));
        model.addAttribute("endTime", DateUtils.formatSimpleDate(new Date()));

        return "/home/index";
    }

    @RequestMapping("/record")
    public void record(HttpSession session,HttpServletResponse response, String startTime, String endTime) {
        if (isEmpty(startTime)) {
            startTime = DateUtils.getCurrDayPrevDay(7);
        }
        if (isEmpty(endTime)) {
            endTime = DateUtils.formatSimpleDate(new Date());
        }
        //成功失败折线图数据
        List<ChartVo> voList = recordService.getRecord(session,startTime, endTime);
        if (isEmpty(voList)) {
            WebUtils.writeJson(response, "null");
        } else {
            WebUtils.writeJson(response, JSON.toJSONString(voList));
        }
    }

    @RequestMapping("/progress")
    public void progress(HttpSession session,HttpServletResponse response) {
        //成功失败折线图数据
        ChartVo chartVo = recordService.getAsProgress(session);
        if (isEmpty(chartVo)) {
            WebUtils.writeJson(response, "null");
        } else {
            WebUtils.writeJson(response, JSON.toJSONString(chartVo));
        }
    }

    @RequestMapping("/monitor")
    public void port(HttpServletResponse response, Long agentId) throws Exception {
        Agent agent = agentService.getAgent(agentId);
        Response req = executeService.monitor(agent);
        /**
         * 直联
         */

        String format = "%d_%s";

        if (agent.getProxy().equals(Opencron.ConnType.CONN.getType())) {
            String port = req.getResult().get("port");
            String url = String.format("http://%s:%s", agent.getIp(), port);
            WebUtils.writeHtml(response, String.format(format, agent.getProxy(), url));
        } else {//代理
            WebUtils.writeHtml(response, String.format(format, agent.getProxy(), JSON.toJSONString(req.getResult())));
        }
    }

    @RequestMapping("/login")
    public void login(HttpSession session,HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, @RequestParam String username, @RequestParam String password) throws Exception {

        //用户信息验证
        int status = homeService.checkLogin(request, username, password);

        if (status == 500) {
            WebUtils.writeJson(response, "{\"msg\":\"用户名密码错误\"}");
            return;
        }
        if (status == 200) {
            //登陆成功了则生成csrf...
            String csrf = OpencronTools.getCSRF(session);
            logger.info("[opencron]login seccussful,generate csrf:{}",csrf);

            User user = OpencronTools.getUser(session);
            //提示用户更改默认密码
            byte[] salt = Encodes.decodeHex(user.getSalt());
            byte[] hashPassword = Digests.sha1(DigestUtils.md5Hex("opencron").toUpperCase().getBytes(), salt, 1024);
            String hashPass = Encodes.encodeHex(hashPassword);

            String format = "{\"status\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\"}";

            if (user.getUserName().equals("opencron") && user.getPassword().equals(hashPass)) {
                WebUtils.writeJson(response, String.format(format, "edit", "userId", user.getUserId(),"csrf",csrf));
                return;
            }

            if (user.getHeaderpic() != null) {
                String name = user.getUserId() + "_140" + user.getPicExtName();
                String path = httpSession.getServletContext().getRealPath(File.separator) + "upload" + File.separator + name;
                IOUtils.writeFile(new File(path), user.getHeaderpic().getBinaryStream());
                user.setHeaderPath(WebUtils.getWebUrlPath(request) + "/upload/" + name);
            }
            WebUtils.writeJson(response, String.format(format, "success", "url", "/home?csrf="+csrf,"csrf",csrf));
            return;
        }
    }


    @RequestMapping("/logout")
    public String logout(HttpSession httpSession) throws Exception {
        OpencronTools.invalidSession(httpSession);
        return "redirect:/";
    }

    @RequestMapping("/headpic/upload")
    public void upload(@RequestParam(value = "file", required = false) MultipartFile file,Long userId, String data, HttpServletRequest request, HttpSession httpSession, HttpServletResponse response) throws Exception {

        String extensionName = null;
        if (file != null)  {
            extensionName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            extensionName = extensionName.replaceAll("\\?\\d+$", "");
        }

        String successFormat = "{\"result\":\"%s\",\"state\":200}";
        String errorFormat = "{\"message\":\"%s\",\"state\":500}";

        Cropper cropper = JSON.parseObject( data, Cropper.class);

        //检查后缀
        if (!".BMP,.JPG,.JPEG,.PNG,.GIF".contains(extensionName.toUpperCase())) {
            WebUtils.writeJson(response, String.format(errorFormat, "格式错误,请上传(bmp,jpg,jpeg,png,gif)格式的图片"));
            return;
        }

        User user = userService.getUserById(userId);

        if (user == null) {
            WebUtils.writeJson(response, String.format(errorFormat, "用户信息获取失败"));
            return;
        }

        String path = httpSession.getServletContext().getRealPath("/") + "upload" + File.separator;

        String picName = user.getUserId() + extensionName.toLowerCase();

        File picFile = new File(path, picName);
        if (!picFile.exists()) {
            picFile.mkdirs();
        }

        try {
            file.transferTo(picFile);
            //检查文件是不是图片
            Image image = ImageIO.read(picFile);
            if (image == null) {
                WebUtils.writeJson(response, String.format(errorFormat, "格式错误,正确的图片"));
                picFile.delete();
                return;
            }

            //检查文件大小
            if (picFile.length() / 1024 / 1024 > 5) {
                WebUtils.writeJson(response, String.format(errorFormat, "文件错误,上传图片大小不能超过5M"));
                picFile.delete();
                return;
            }

            //旋转并且裁剪
            ImageUtils.instance(picFile).rotate(cropper.getRotate()).clip(cropper.getX(),cropper.getY(),cropper.getWidth(),cropper.getHeight()).build();

            //保存入库.....
            userService.uploadimg(picFile, userId);
            userService.updateUser(user);

            String contextPath = WebUtils.getWebUrlPath(request);
            String imgPath = contextPath + "/upload/" + picName + "?" + System.currentTimeMillis();
            user.setHeaderPath(imgPath);
            user.setHeaderpic(null);
            httpSession.setAttribute(OpencronTools.LOGIN_USER, user);

            WebUtils.writeJson(response, String.format(successFormat, imgPath));
            logger.info(" upload file successful @ " + picName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("upload exception:" + e.getMessage());
        }
    }


    @RequestMapping("/notice/view")
    public String log(HttpSession session,Model model, PageBean pageBean, Long agentId, String sendTime) {
        model.addAttribute("agents", agentService.getOwnerAgents(session));
        if (notEmpty(agentId)) {
            model.addAttribute("agentId", agentId);
        }
        if (notEmpty(sendTime)) {
            model.addAttribute("sendTime", sendTime);
        }
        homeService.getLog(session,pageBean, agentId, sendTime);
        return "notice/view";
    }


    @RequestMapping("/notice/uncount")
    public void uncount(HttpSession session,HttpServletResponse response) {
        Long count = homeService.getUnReadCount(session);
        WebUtils.writeHtml(response, count.toString());
    }

    /**
     * 未读取的站类信
     * @param model
     * @return
     */
    @RequestMapping("/notice/unread")
    public String nuread(HttpSession session,Model model) {
        model.addAttribute("message", homeService.getUnReadMessage(session));
        return "notice/info";
    }

    @RequestMapping("/notice/detail")
    public String detail(Model model, Long logId) {
        model.addAttribute("sender", configService.getSysConfig().getSenderEmail());
        model.addAttribute("log", homeService.getLogDetail(logId));
        homeService.updateAfterRead(logId);
        return "notice/detail";
    }
}
