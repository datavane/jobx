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

import com.jobxhub.common.Constants;
import com.jobxhub.common.util.CommonUtils;
import com.jobxhub.common.util.collection.ParamsMap;
import com.jobxhub.server.annotation.RequestRepeat;
import com.jobxhub.server.dto.Terminal;

import com.jobxhub.server.support.*;
import com.jobxhub.server.service.TerminalService;
import com.jobxhub.server.tag.PageBean;
import com.jobxhub.server.dto.Status;
import com.jobxhub.server.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * benjobs..
 */
@Controller
@RequestMapping("terminal")
public class TerminalController {

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private TerminalContext terminalContext;

    @Autowired
    private TerminalClusterProcessor terminalClusterProcessor;

    @Autowired
    private TerminalOneProcessor terminalOneProcessor;

    @RequestMapping(value = "ssh.do", method = RequestMethod.POST)
    @ResponseBody
    public synchronized Map<String, String> ssh(HttpSession session, Terminal terminal) {
        User user = JobXTools.getUser(session);

        terminal = terminalService.getById(terminal.getId());

        Terminal.AuthStatus authStatus = terminalService.auth(terminal);
        //登陆认证成功
        if (authStatus.equals(Terminal.AuthStatus.SUCCESS)) {
            String token = CommonUtils.uuid();
            terminal.setUser(user);
            terminalContext.put(token, terminal);
            return ParamsMap.map()
                    .set("status", "success")
                    .set("url", "/terminal/open.htm?token=" + token);
        } else {
            return ParamsMap.map().set("status", authStatus.status);
        }
    }

    @RequestMapping("ssh2.htm")
    public synchronized String ssh2(HttpSession session, Terminal terminal) {
        User user = JobXTools.getUser(session);

        terminal = terminalService.getById(terminal.getId());
        Terminal.AuthStatus authStatus = terminalService.auth(terminal);
        //登陆认证成功
        if (authStatus.equals(Terminal.AuthStatus.SUCCESS)) {
            String token = CommonUtils.uuid();
            terminal.setUser(user);
            terminalContext.put(token, terminal);
            return "redirect:/terminal/open.htm?token=" + token;
        } else {
            //重新输入密码进行认证...
            return "redirect:/terminal/open.htm?id=" + terminal.getId();
        }
    }

    @RequestMapping(value = "detail.do", method = RequestMethod.POST)
    @ResponseBody
    public Terminal detail(Terminal terminal) {
        return terminalService.getById(terminal.getId());
    }

    @RequestMapping(value = "exists.do", method = RequestMethod.POST)
    @ResponseBody
    public boolean exists(Terminal terminal) throws Exception {
        return terminalService.exists(terminal.getUserName(), terminal.getHost());
    }

    @RequestMapping("view.htm")
    public String view(HttpSession session, PageBean pageBean, Model model) {
        terminalService.getPageBean(pageBean, JobXTools.getUserId(session));
        return "/terminal/view";
    }

    @RequestMapping("open.htm")
    public String open(HttpSession session, Model model, String token, Long id) {
        //登陆失败
        if (token == null && id != null) {
            Terminal terminal = terminalService.getById(id);
            model.addAttribute("terminal", terminal);
            return "/terminal/error";
        }
        Terminal terminal = terminalContext.get(token);
        if (terminal != null) {
            model.addAttribute("name", terminal.getName() + "(" + terminal.getHost() + ")");
            model.addAttribute("token", token);
            model.addAttribute("id", terminal.getId());
            model.addAttribute("theme", terminal.getTheme());
            List<Terminal> terminas = terminalService.getByUser(terminal.getUserId());
            model.addAttribute("terms", terminas);
            //注册实例
            terminalClusterProcessor.registry(token);
            return "/terminal/console";
        }
        return "/terminal/error";
    }

    /**
     * 不能重复复制会话,可以通过ajax的方式重新生成token解决....
     *
     * @param id
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping("reopen.htm")
    public String reopen(Long id, String token) {
        String reKey = id + "_" + token;
        Terminal terminal = terminalContext.remove(reKey);//reKey
        if (terminal != null) {
            token = CommonUtils.uuid();
            terminalContext.put(token, terminal);
            return "redirect:/terminal/open.htm?token=" + token;
        }
        return "/terminal/error";
    }

    @RequestMapping(value = "resize.do", method = RequestMethod.POST)
    @ResponseBody
    public Status resize(String token, Integer cols, Integer rows, Integer width, Integer height) throws Exception {
        if (!Constants.JOBX_CLUSTER) {
            return terminalOneProcessor.resize(token, cols, rows, width, height);
        }
        Status status = Status.TRUE;
        terminalClusterProcessor.doWork("resize", status, token, cols, rows, width, height);
        return status;
    }

    @RequestMapping(value = "sendAll.do", method = RequestMethod.POST)
    @ResponseBody
    public Status sendAll(String token, String cmd) throws Exception {
        if (!Constants.JOBX_CLUSTER) {
            return terminalOneProcessor.sendAll(token, cmd);
        }
        Status status = Status.TRUE;
        terminalClusterProcessor.doWork("sendAll", status, token, cmd);
        return status;
    }

    @RequestMapping(value = "theme.do", method = RequestMethod.POST)
    @ResponseBody
    public Status theme(String token, String theme) throws Exception {
        if (!Constants.JOBX_CLUSTER) {
            return terminalOneProcessor.theme(token, theme);
        }
        Status status = Status.TRUE;
        terminalClusterProcessor.doWork("theme", status, token, theme);
        return status;
    }

    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Status upload(HttpSession httpSession, String token, @RequestParam(value = "file", required = false) MultipartFile file, String path) {
        Status status = Status.TRUE;
        String tmpPath = httpSession.getServletContext().getRealPath("/") + "upload" + File.separator;
        File tempFile = new File(tmpPath, file.getOriginalFilename());
        try {
            file.transferTo(tempFile);
            if (CommonUtils.isEmpty(path)) {
                path = ".";
            } else {
                if (path.endsWith("/")) {
                    path = path.substring(0, path.lastIndexOf("/"));
                }
            }

            if (!Constants.JOBX_CLUSTER) {
                return terminalOneProcessor.upload(token, tempFile, path + "/" + file.getOriginalFilename(), file.getSize());
            }
            terminalClusterProcessor.doWork("upload", status, token, tempFile, path + "/" + file.getOriginalFilename(), file.getSize());
            tempFile.delete();
        } catch (Exception e) {
        }
        return status;
    }

    @RequestMapping(value = "save.do", method = RequestMethod.POST)
    @ResponseBody
    @RequestRepeat(view = true)
    public String save(HttpSession session, Terminal term, @RequestParam(value = "sshkey", required = false) MultipartFile sshkey) throws Exception {
        term.setSshKeyFile(sshkey);
        Terminal.AuthStatus authStatus = terminalService.auth(term);
        if (authStatus.equals(Terminal.AuthStatus.SUCCESS)) {
            User user = JobXTools.getUser(session);
            term.setUserId(user.getUserId());
            terminalService.merge(term);
        }
        return authStatus.status;
    }


    @RequestMapping(value = "delete.do", method = RequestMethod.POST)
    @ResponseBody
    public String delete(HttpSession session, Terminal term) {
        return terminalService.delete(session, term.getId());
    }

}