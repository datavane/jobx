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

import org.opencron.common.utils.CommonUtils;
import org.opencron.server.domain.Terminal;
import org.opencron.server.domain.User;

import org.opencron.server.job.OpencronTools;
import org.opencron.server.service.TerminalService;
import org.opencron.server.tag.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opencron.server.service.TerminalService.*;
import static org.opencron.common.utils.WebUtils.*;

/**
 * benjobs..
 */
@Controller
@RequestMapping("terminal")
public class TerminalController extends BaseController {

    @Autowired
    private TerminalService termService;

    @RequestMapping(value = "ssh.do",method= RequestMethod.POST)
    @ResponseBody
    public Map<String,String> ssh(HttpSession session, HttpServletResponse response, Terminal terminal) throws Exception {
        User user = OpencronTools.getUser(session);

        terminal = termService.getById(terminal.getId());
        Map<String,String> map = new HashMap<String,String>(0);

        Terminal.AuthStatus authStatus = termService.auth(terminal);
        //登陆认证成功
        if (authStatus.equals(Terminal.AuthStatus.SUCCESS)) {
            String token = CommonUtils.uuid();
            terminal.setUser(user);
            TerminalContext.put(token, terminal);
            OpencronTools.setSshSessionId(session, token);
            map.put("status","success");
            map.put("url","/terminal/open.htm?token=" + token + "&csrf=" + OpencronTools.getCSRF(session));
        } else {
            //重新输入密码进行认证...
            map.put("status",authStatus.status);
        }
        return map;
    }

    @RequestMapping("ssh2.htm")
    public String ssh2(HttpSession session, Terminal terminal) throws Exception {
        User user = OpencronTools.getUser(session);

        terminal = termService.getById(terminal.getId());
        Terminal.AuthStatus authStatus = termService.auth(terminal);
        //登陆认证成功
        if (authStatus.equals(Terminal.AuthStatus.SUCCESS)) {
            String token = CommonUtils.uuid();
            terminal.setUser(user);
            TerminalContext.put(token, terminal);
            OpencronTools.setSshSessionId(session, token);
            return "redirect:/terminal/open.htm?token=" + token + "&csrf=" + OpencronTools.getCSRF(session);
        } else {
            //重新输入密码进行认证...
            return "redirect:/terminal/open.htm?id=" + terminal.getId() + "&csrf=" + OpencronTools.getCSRF(session);
        }

    }

    @RequestMapping(value = "detail.do",method= RequestMethod.POST)
    @ResponseBody
    public Terminal detail(Terminal terminal) throws Exception {
        return termService.getById(terminal.getId());
    }

    @RequestMapping(value = "exists.do",method= RequestMethod.POST)
    @ResponseBody
    public boolean exists(HttpSession session,Terminal terminal) throws Exception {
        User user = OpencronTools.getUser(session);
        return termService.exists(user.getUserId(), terminal.getHost());
    }

    @RequestMapping("view.htm")
    public String view(HttpSession session, PageBean pageBean, Model model) throws Exception {
        pageBean = termService.getPageBeanByUser(pageBean, OpencronTools.getUserId(session));
        model.addAttribute("pageBean", pageBean);
        return "/terminal/view";
    }

    @RequestMapping("open.htm")
    public String open(HttpServletRequest request, String token, Long id) throws Exception {
        //登陆失败
        if (token == null && id != null) {
            Terminal terminal = termService.getById(id);
            request.setAttribute("terminal", terminal);
            return "/terminal/error";
        }
        Terminal terminal = TerminalContext.get(token);
        if (terminal != null) {
            request.setAttribute("name", terminal.getName() + "(" + terminal.getHost() + ")");
            request.setAttribute("token", token);
            request.setAttribute("theme", terminal.getTheme());
            List<Terminal> terminas = termService.getListByUser(terminal.getUser());
            request.setAttribute("terms", terminas);
            return "/terminal/console";
        }
        return "/terminal/error";
    }

    @RequestMapping("reopen.htm")
    public String reopen(HttpSession session, String token) throws Exception {
        Terminal terminal = (Terminal) OpencronTools.CACHE.get(token);
        if (terminal != null) {
            token = CommonUtils.uuid();
            TerminalContext.put(token, terminal);
            session.setAttribute(OpencronTools.SSH_SESSION_ID, token);
            return "redirect:/terminal/open.htm?token=" + token + "&csrf=" + OpencronTools.getCSRF(session);
        }
        return "/terminal/error";
    }

    @RequestMapping(value = "resize.do",method= RequestMethod.POST)
    @ResponseBody
    public boolean resize(String token, Integer cols, Integer rows, Integer width, Integer height) throws Exception {
        TerminalClient terminalClient = TerminalSession.get(token);
        if (terminalClient != null) {
            terminalClient.resize(cols, rows, width, height);
        }
        return true;
    }

    @RequestMapping(value = "sendAll.do",method= RequestMethod.POST)
    @ResponseBody
    public boolean sendAll(String token, String cmd) throws Exception {
        cmd = URLDecoder.decode(cmd, "UTF-8");
        TerminalClient terminalClient = TerminalSession.get(token);
        if (terminalClient != null) {
            List<TerminalClient> terminalClients = TerminalSession.findClient(terminalClient.getHttpSessionId());
            for (TerminalClient client : terminalClients) {
                client.write(cmd);
            }
        }
        return true;
    }

    @RequestMapping(value = "theme.do",method= RequestMethod.POST)
    public void theme(String token, String theme) throws Exception {
        TerminalClient terminalClient = TerminalSession.get(token);
        if (terminalClient != null) {
            termService.theme(terminalClient.getTerminal(), theme);
        }
    }

    @RequestMapping(value = "upload.do",method= RequestMethod.POST)
    public void upload(HttpSession httpSession, HttpServletResponse response, String token, @RequestParam(value = "file", required = false) MultipartFile[] file, String path) {
        TerminalClient terminalClient = TerminalSession.get(token);
        boolean success = true;
        if (terminalClient != null) {
            for (MultipartFile ifile : file) {
                String tmpPath = httpSession.getServletContext().getRealPath("/") + "upload" + File.separator;
                File tempFile = new File(tmpPath, ifile.getOriginalFilename());
                try {
                    ifile.transferTo(tempFile);
                    if (CommonUtils.isEmpty(path)) {
                        path = ".";
                    } else {
                        if (path.endsWith("/")) {
                            path = path.substring(0, path.lastIndexOf("/"));
                        }
                    }
                    terminalClient.upload(tempFile.getAbsolutePath(), path + "/" + ifile.getOriginalFilename(), ifile.getSize());
                    tempFile.delete();
                } catch (Exception e) {
                    success = false;
                }
            }
        }
        writeJson(response, String.format("{\"success\":\"%s\"}", success ? "true" : "false"));
    }


    @RequestMapping(value = "save.do",method= RequestMethod.POST)
    @ResponseBody
    public String save(HttpSession session,Terminal term) throws Exception {
        Terminal.AuthStatus authStatus = termService.auth(term);
        if (authStatus.equals(Terminal.AuthStatus.SUCCESS)) {
            User user = OpencronTools.getUser(session);
            term.setUserId(user.getUserId());
            termService.merge(term);
        }
        return authStatus.status;
    }


    @RequestMapping(value = "delete.do",method= RequestMethod.POST)
    @ResponseBody
    public String delete(HttpSession session, Terminal term) throws Exception {
        return termService.delete(session, term.getId());
    }

}