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
package com.jobxhub.server.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.base.Function;
import com.jobxhub.common.util.DigestUtils;
import com.jobxhub.common.util.RSAUtils;
import com.jobxhub.server.domain.TerminalBean;
import com.jobxhub.server.support.JobXTools;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class Terminal implements Serializable {

    private Long id;
    private String name;
    private Long userId;
    private String host;
    private int port;
    private String userName;
    private String theme;
    private Integer sshType;//0账户登录,1:sshKey登录

    @JSONField(serialize = false)
    private byte[] privateKey;

    @JSONField(serialize = false)
    private byte[] passphrase;

    @JSONField(serialize = false)
    private byte[] authorization;

    private String status = Terminal.AuthStatus.SUCCESS.status;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    @JSONField(serialize = false)
    private User user;

    @JSONField(serialize = false)
    private String password;

    @JSONField(serialize = false)
    private String phrase;

    private String clientId;

    private MultipartFile sshKeyFile;


    public static Function<? super TerminalBean, ? extends Terminal> transfer = new Function<TerminalBean, Terminal>() {
        @Override
        public Terminal apply(TerminalBean input) {
            return new Terminal(input);
        }
    };

    public Terminal(){}

    public Terminal(TerminalBean terminalBean){
        BeanUtils.copyProperties(terminalBean,this);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        try {
            byte[] decodedData = RSAUtils.decryptByPrivateKey(this.authorization, JobXTools.Auth.getPrivateKey());
            return new String(decodedData);
        } catch (Exception e) {
        }
        return null;
    }

    public void setPassword(String password) throws Exception {
        if (password != null) {
            password = DigestUtils.passBase64(password);
        }
        this.authorization = RSAUtils.encryptByPublicKey(password.getBytes(), JobXTools.Auth.getPublicKey());
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date logintime) {
        this.loginTime = logintime;
    }

    public byte[] getAuthorization() {
        return authorization;
    }

    public void setAuthorization(byte[] authorization) {
        this.authorization = authorization;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Integer getSshType() {
        return sshType;
    }

    public void setSshType(Integer sshType) {
        this.sshType = sshType;
    }

    public byte[] getPrivateKey() {
        //拿本地的公钥解密ssh的私钥
        if (this.privateKey != null) {
            try {
                return RSAUtils.decryptByPrivateKey(this.privateKey, JobXTools.Auth.getPrivateKey());
            } catch (Exception e) {
                throw new RuntimeException("[JobX] getPrivateKey error!");
            }
        }
        return null;
    }

    public void setPrivateKey(byte[] privateKey) throws Exception {
        //拿本地的公钥加密ssh的私钥
        if (privateKey!=null) {
            this.privateKey = RSAUtils.encryptByPublicKey(privateKey, JobXTools.Auth.getPublicKey());
        }
    }

    public byte[] getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(byte[] passphrase) {
        this.passphrase = passphrase;
    }

    public String getPhrase() {
        if (this.passphrase!=null) {
            try {
                byte[] decodedData = RSAUtils.decryptByPrivateKey(this.passphrase, JobXTools.Auth.getPrivateKey());
                return new String(decodedData);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public void setPhrase(String phrase) throws Exception {
        if (phrase != null) {
            this.passphrase = RSAUtils.encryptByPublicKey(phrase.getBytes(), JobXTools.Auth.getPublicKey());
        }
    }

    public String getPrivateKeyPath() {
        return JobXTools.Auth.getKeyPath().concat("/").concat(this.getHost()).concat("@").concat(this.getUserName()).concat("_id_rsa");
    }

    public MultipartFile getSshKeyFile() {
        return sshKeyFile;
    }

    public void setSshKeyFile(MultipartFile sshKeyFile) {
        this.sshKeyFile = sshKeyFile;
    }

    @Override
    public String toString() {
        return "Terminal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", userName='" + userName + '\'' +
                ", theme='" + theme + '\'' +
                ", sshType='" + sshType + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", passphrase='" + passphrase + '\'' +
                ", authorization=" + Arrays.toString(authorization) +
                ", status='" + status + '\'' +
                ", loginTime=" + loginTime +
                ", user=" + user +
                ", password='" + password + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Terminal terminal = (Terminal) o;

        if (userName != null ? !userName.equals(terminal.userName) : terminal.userName != null) return false;
        return user != null ? user.equals(terminal.user) : terminal.user == null;
    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    public enum AuthStatus {
        INITIAL("initial"),
        AUTH_FAIL("authfail"),
        PUBLIC_KEY_FAIL("keyauthfail"),
        GENERIC_FAIL("genericfail"),
        HOST_FAIL("hostfail"),
        SUCCESS("success");
        public String status;

        AuthStatus(String status) {
            this.status = status;
        }

    }

}
