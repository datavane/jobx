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
import org.opencron.common.utils.CommonUtils;
import org.opencron.common.utils.WebUtils;
import org.opencron.server.domain.Terminal;
import org.opencron.server.job.OpencronTools;
import org.opencron.server.domain.User;
import org.opencron.server.service.TerminalService;

import org.opencron.server.tag.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.File;
import java.net.URLDecoder;
import java.util.List;

import static org.opencron.server.service.TerminalService.*;

/**
 * benjobs..
 */
@Controller
@RequestMapping("/terminal")
public class TerminalController  extends BaseController{

    @Autowired
    private TerminalService termService;

    @RequestMapping("/ssh")
    public void ssh(HttpSession session,HttpServletResponse response, Terminal terminal) throws Exception {
        User user = OpencronTools.getUser(session);

        String json = "{status:'%s',url:'%s'}";

        terminal = termService.getById(terminal.getId());
        Terminal.AuthStatus authStatus = termService.auth(terminal);
        //登陆认证成功
        if (authStatus.equals(Terminal.AuthStatus.SUCCESS)) {
            String token = CommonUtils.uuid();
            terminal.setUser(user);
            TerminalContext.put(token,terminal);
            OpencronTools.setSshSessionId(session,token);
            WebUtils.writeJson(response, String.format(json,"success","/terminal/open?token="+token+"&csrf="+OpencronTools.getCSRF(session)));
        }else {
            //重新输入密码进行认证...
            WebUtils.writeJson(response, String.format(json,authStatus.status,"null"));
            return;
        }
    }

    @RequestMapping("/ssh2")
    public String ssh2(HttpSession session,Terminal terminal) throws Exception {
        User user = OpencronTools.getUser(session);

        terminal = termService.getById(terminal.getId());
        Terminal.AuthStatus authStatus = termService.auth(terminal);
        //登陆认证成功
        if (authStatus.equals(Terminal.AuthStatus.SUCCESS)) {
            String token = CommonUtils.uuid();
            terminal.setUser(user);
            TerminalContext.put(token,terminal);
            OpencronTools.setSshSessionId(session,token);
            return "redirect:/terminal/open?token="+token+"&csrf="+ OpencronTools.getCSRF(session);
        }else {
            //重新输入密码进行认证...
            return "redirect:/terminal/open?id="+terminal.getId()+"&csrf="+ OpencronTools.getCSRF(session);
        }

    }

    @RequestMapping("/detail")
    public void detail(HttpServletResponse response,Terminal terminal) throws Exception {
        terminal = termService.getById(terminal.getId());
        WebUtils.writeJson(response, JSON.toJSONString(terminal));
    }

    @RequestMapping("/exists")
    public void exists(HttpSession session,HttpServletResponse response,Terminal terminal) throws Exception {
        User user = OpencronTools.getUser(session);
        boolean exists = termService.exists( user.getUserId(),terminal.getHost());
        WebUtils.writeHtml(response,exists?"true":"false");
    }

    @RequestMapping("/view")
    public String view(HttpSession session,PageBean pageBean,Model model ) throws Exception {
        pageBean = termService.getPageBeanByUser(pageBean, OpencronTools.getUserId(session));
        model.addAttribute("pageBean",pageBean);
        return "/terminal/view";
    }

    @RequestMapping("/open")
    public String open(HttpServletRequest request,String token,Long id) throws Exception {
        //登陆失败
        if (token==null && id!=null) {
            Terminal terminal = termService.getById(id);
            request.setAttribute("terminal",terminal);
            return "/terminal/error";
        }
        Terminal terminal = TerminalContext.get(token);
        if (terminal!=null) {
            request.setAttribute("name",terminal.getName()+"("+terminal.getHost()+")");
            request.setAttribute("token",token);
            request.setAttribute("theme",terminal.getTheme());
            List<Terminal> terminas = termService.getListByUser(terminal.getUser());
            request.setAttribute("terms",terminas);
            return "/terminal/console";
        }
        return "/terminal/error";
    }

    @RequestMapping("/reopen")
    public String reopen(HttpSession session,String token ) throws Exception {
        Terminal terminal = (Terminal) OpencronTools.CACHE.get(token);
        if (terminal!=null) {
            token = CommonUtils.uuid();
            TerminalContext.put(token,terminal);
            session.setAttribute(OpencronTools.SSH_SESSION_ID,token);
            return "redirect:/terminal/open?token="+token+"&csrf="+ OpencronTools.getCSRF(session);
        }
        return "/terminal/error";
    }

    @RequestMapping("/resize")
    public void resize(HttpServletResponse response, String token,Integer cols,Integer rows,Integer width,Integer height) throws Exception {
        TerminalClient terminalClient = TerminalSession.get(token);
        if (terminalClient!=null) {
            terminalClient.resize(cols,rows,width,height);
        }
        WebUtils.writeHtml(response,"");
    }

    @RequestMapping("/sendAll")
    public void sendAll(String token,String cmd) throws Exception {
        cmd =  URLDecoder.decode(cmd,"UTF-8");
        TerminalClient terminalClient = TerminalSession.get(token);
        if (terminalClient!=null) {
            List<TerminalClient> terminalClients = TerminalSession.findClient(terminalClient.getHttpSessionId());
            for (TerminalClient client:terminalClients) {
                client.write(cmd);
            }
        }
    }

    @RequestMapping("/theme")
    public void theme(HttpServletResponse response, String token,String theme) throws Exception {
        TerminalClient terminalClient = TerminalSession.get(token);
        if (terminalClient!=null) {
            termService.theme(terminalClient.getTerminal(),theme);
        }
        WebUtils.writeHtml(response,"");
    }

    @RequestMapping("/upload")
    public void upload(HttpSession httpSession,HttpServletResponse response, String token,@RequestParam(value = "file", required = false) MultipartFile[] file,String path) {
        TerminalClient terminalClient = TerminalSession.get(token);
        boolean success = true;
        if (terminalClient!=null) {
            for(MultipartFile ifile : file){
                String tmpPath = httpSession.getServletContext().getRealPath("/") + "upload" + File.separator;
                File tempFile = new File(tmpPath, ifile.getOriginalFilename());
                try {
                    ifile.transferTo(tempFile);
                    if (CommonUtils.isEmpty(path)) {
                        path = ".";
                    }else {
                        if (path.endsWith("/")) {
                            path = path.substring(0, path.lastIndexOf("/"));
                        }
                    }
                    terminalClient.upload(tempFile.getAbsolutePath(),path+"/"+ifile.getOriginalFilename(),ifile.getSize());
                    tempFile.delete();
                }catch (Exception e) {
                    success = false;
                }
            }
        }
        WebUtils.writeJson(response,String.format("{\"success\":\"%s\"}",success?"true":"false"));
    }


    @RequestMapping("/save")
    public void save(HttpSession session,HttpServletResponse response, Terminal term) throws Exception {
        Terminal.AuthStatus authStatus = termService.auth(term);
        if (authStatus.equals(Terminal.AuthStatus.SUCCESS)) {
            User user = OpencronTools.getUser(session);
            term.setUserId(user.getUserId());
            termService.saveOrUpdate(term);
        }
        WebUtils.writeHtml(response,authStatus.status);
    }


    @RequestMapping("/del")
    public void del(HttpSession session,HttpServletResponse response, Terminal term) throws Exception {
        String message = termService.delete(session,term.getId());
        WebUtils.writeHtml(response,message);
    }

}