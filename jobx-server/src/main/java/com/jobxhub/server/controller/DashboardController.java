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

import com.alibaba.fastjson.JSON;
import com.jobxhub.common.Constants;
import com.jobxhub.common.job.Response;
import com.jobxhub.common.util.*;
import com.jobxhub.common.util.collection.HashMap;
import com.jobxhub.common.util.collection.ParamsMap;
import com.jobxhub.server.annotation.RequestRepeat;
import com.jobxhub.server.support.JobXTools;
import com.jobxhub.server.service.*;
import com.jobxhub.server.tag.PageBean;
import com.jobxhub.server.dto.*;
import com.jobxhub.server.util.PropertyPlaceholder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.Image;
import java.io.File;
import java.io.Serializable;
import java.util.*;

import static com.jobxhub.common.util.CommonUtils.isEmpty;
import static com.jobxhub.common.util.CommonUtils.notEmpty;
import static com.jobxhub.common.util.WebUtils.*;

/**
 * Created by ChenHui on 2016/2/17.
 */
@Controller
public class DashboardController {

    @Autowired
    private LogService logService;

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

    @Autowired
    private PropertyPlaceholder propertyPlaceholder;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("")
    public String index() {
        return "/home/login";
    }

    @RequestMapping("repeat")
    public String repeat(){
        return "/error/repeat";
    }

    @RequestMapping("dashboard.htm")
    public String dashboard(HttpSession session, Model model) {
        /**
         * agent...
         */
        int success = agentService.getCountByStatus(session, Constants.ConnStatus.CONNECTED);
        int failed = agentService.getCountByStatus(session, Constants.ConnStatus.DISCONNECTED);
        model.addAttribute("success", success);
        model.addAttribute("failed", failed);

        model.addAttribute("agents", agentService.getOwnerAgents(session));
        /**
         * job
         */
        int singleton = jobService.getCountByType(session, Constants.JobType.SIMPLE);
        int flow = jobService.getCountByType(session, Constants.JobType.FLOW);

        model.addAttribute("singleton", singleton);
        model.addAttribute("flow", flow);
        model.addAttribute("job", singleton+flow);

        /**
         * 成功作业,自动执行
         */
        Integer successAutoRecord = recordService.getRecordCount(session, Constants.ResultStatus.SUCCESSFUL, Constants.ExecType.AUTO);
        Integer successOperRecord = recordService.getRecordCount(session, Constants.ResultStatus.SUCCESSFUL, Constants.ExecType.OPERATOR);
        Integer successBatchRecord = recordService.getRecordCount(session, Constants.ResultStatus.SUCCESSFUL, Constants.ExecType.BATCH);

        model.addAttribute("successAutoRecord", successAutoRecord);
        model.addAttribute("successOperRecord", successOperRecord + successBatchRecord);
        model.addAttribute("successRecord", successAutoRecord + successOperRecord + successBatchRecord);

        /**
         * 失败作业
         */
        Integer failedAutoRecord = recordService.getRecordCount(session, Constants.ResultStatus.FAILED, Constants.ExecType.AUTO);
        Integer failedOperRecord = recordService.getRecordCount(session, Constants.ResultStatus.FAILED, Constants.ExecType.OPERATOR);
        Integer failedBatchRecord = recordService.getRecordCount(session, Constants.ResultStatus.FAILED, Constants.ExecType.BATCH);

        model.addAttribute("failedAutoRecord", failedAutoRecord);
        model.addAttribute("failedOperRecord", failedOperRecord + failedBatchRecord);
        model.addAttribute("failedRecord", failedAutoRecord + failedOperRecord + failedBatchRecord);

        model.addAttribute("startTime", DateUtils.getCurrDayPrevDay(7));
        model.addAttribute("endTime", DateUtils.formatSimpleDate(new Date()));

        return "/home/index";
    }

    @RequestMapping("record.do")
    @ResponseBody
    public List<Chart> record(HttpSession session, String startTime, String endTime) {
        if (isEmpty(startTime)) {
            startTime = DateUtils.getCurrDayPrevDay(7);
        }
        if (isEmpty(endTime)) {
            endTime = DateUtils.formatSimpleDate(new Date());
        }
        //成功失败折线图数据
        List<Chart> infoList = recordService.getReportChart(session, startTime, endTime);
        if (isEmpty(infoList)) {
            return Collections.emptyList();
        } else {
            return infoList;
        }
    }

    @RequestMapping(value = "progress.do", method = RequestMethod.POST)
    @ResponseBody
    public Chart progress(HttpSession session) {
        //成功失败折线图数据
        Chart chart = recordService.getTopChart(session);
        if (isEmpty(chart)) {
            return null;
        }

        return chart;
    }

    @RequestMapping(value = "monitor.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Serializable> port(Long agentId) throws Exception {
        final Agent agent = agentService.getAgent(agentId);
        /**
         * 直联
         */
        if (agent.getProxyId()==null) {
            final String url = String.format("http://%s:%s", agent.getHost(), propertyPlaceholder.getMonitorPort());
            return new HashMap<String, Serializable>() {{
                put("connType", Constants.ConnType.CONN.getType());
                put("data", url);
            }};
        }else {
            final Response resp = executeService.monitor(agent);
            return new HashMap<String, Serializable>() {{
                put("connType", Constants.ConnType.PROXY.getType());
                put("data", JSON.toJSONString(resp.getResult()));
            }};
        }
    }

    @RequestMapping(value = "headpic/upload.do", method = RequestMethod.POST)
    @ResponseBody
    @RequestRepeat
    public Map upload(@RequestParam(value = "file", required = false) MultipartFile file, Long userId, String data, HttpServletRequest request, HttpSession httpSession) throws Exception {

        String extensionName = null;
        if (file != null) {
            extensionName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            extensionName = extensionName.replaceAll("\\?\\d+$", "");
        }

        Cropper cropper = JSON.parseObject(DigestUtils.passBase64(data), Cropper.class);
        ParamsMap result = ParamsMap.map();
        //检查后缀
        if (!".BMP,.JPG,.JPEG,.PNG,.GIF".contains(extensionName.toUpperCase())) {
            return result.set("message", "格式错误,请上传(bmp,jpg,jpeg,png,gif)格式的图片").set("state", 500);
        }

        User user = userService.getUserById(userId);

        if (user == null) {
            return result.set("message", "用户信息获取失败").set("state", 500);
        }

        String rootPath = httpSession.getServletContext().getRealPath("/");
        String path = rootPath.replaceFirst("/$", "") + "/upload/";

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
                picFile.delete();
                return result.set("message", "格式错误,解析失败,请上传正确的图片").set("state", 500);
            }

            //检查文件大小
            if (picFile.length() / 1024 / 1024 > 5) {
                picFile.delete();
                return result.set("message", "文件错误,上传图片大小不能超过5M").set("state", 500);
            }

            //旋转并且裁剪
            ImageUtils.getInstance(picFile).rotate(cropper.getRotate()).clip(cropper.getX(), cropper.getY(), cropper.getWidth(), cropper.getHeight()).build();

            //保存入库.....
            userService.uploadImg(userId,picFile);

            String contextPath = getWebUrlPath(request);
            String imgPath = contextPath + "/upload/" + picName + "?" + System.currentTimeMillis();
            user.setHeaderPath(imgPath);
            user.setHeaderPic(null);
            httpSession.setAttribute(Constants.PARAM_LOGIN_USER_KEY, user);

            logger.info(" upload file successful @ " + picName);

            return result.set("result", imgPath).set("state", 200);

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("upload exception:" + e.getMessage());
        }

        return result.set("message", "未知错误,上传失败").set("state", 500);
    }


    @RequestMapping("notice/view.htm")
    public String log(HttpSession session, Model model, PageBean pageBean, Long agentId, String sendTime) {
        model.addAttribute("agents", agentService.getOwnerAgents(session));
        if (notEmpty(agentId)) {
            model.addAttribute("agentId", agentId);
        }
        if (notEmpty(sendTime)) {
            model.addAttribute("sendTime", sendTime);
        }
        logService.getByPageBean(session, pageBean, agentId, sendTime);
        return "notice/view";
    }


    @RequestMapping(value = "notice/uncount.do", method = RequestMethod.POST)
    @ResponseBody
    public Integer uncount(HttpSession session) {
        Long userId = JobXTools.getUserId(session);
        return logService.getUnReadCount(userId);
    }

    /**
     * 未读取的站类信
     *
     * @param model
     * @return
     */
    @RequestMapping("notice/unread.htm")
    public String nuread(HttpSession session, Model model) {
        Long userId = JobXTools.getUserId(session);
        model.addAttribute("message", logService.getUnReadMessage(userId));
        return "notice/info";
    }

    @RequestMapping("notice/detail/{logId}.htm")
    public String detail(Model model, @PathVariable("logId") Long logId) {
        Log log = logService.getById(logId);
        if (log == null) {
            return "/error/404";
        }
        model.addAttribute("sender", configService.getSysConfig().getSenderEmail());
        model.addAttribute("log", log);
        logService.updateAfterRead(logId);
        return "notice/detail";
    }
}
