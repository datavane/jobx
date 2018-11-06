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

package com.jobxhub.core.dto;


import java.io.Serializable;

public class Status implements Serializable {

    private boolean status;

    public Status(boolean status) {
        this.status = status;
    }

    public static Status FALSE = new Status(false);

    public static Status TRUE = new Status(true);

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public static Status getFALSE() {
        return FALSE;
    }

    public static void setFALSE(Status FALSE) {
        Status.FALSE = FALSE;
    }

    public static Status getTRUE() {
        return TRUE;
    }

    public static void setTRUE(Status TRUE) {
        Status.TRUE = TRUE;
    }

    public static Status create(boolean b) {
        return new Status(b);
    }
}
