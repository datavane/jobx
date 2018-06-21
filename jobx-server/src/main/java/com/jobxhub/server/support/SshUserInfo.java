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

package com.jobxhub.server.support;

import com.jcraft.jsch.UserInfo;

public class SshUserInfo implements UserInfo {
    //private String passphrase = null;

    public SshUserInfo() {
        //this.passphrase = passphrase;
    }

    public String getPassphrase() {
        return null;//passphrase;
    }

    public String getPassword() {
        return null;
    }

    public boolean promptPassphrase(String s) {
        return true;
    }

    public boolean promptPassword(String s) {
        return true;
    }

    public boolean promptYesNo(String s) {
        return true;
    }

    public void showMessage(String s) {
        System.out.println(s);
    }
}