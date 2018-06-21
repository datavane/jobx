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
package com.jobxhub.common.exception;

/**
 * 未知异常
 *
 * @author wanghuajie 2012.8.23
 */
public class UnknownException extends BasicException {

    private static final long serialVersionUID = 9108301934211924250L;

    public UnknownException() {
        super();
    }

    public UnknownException(String msg) {
        super(msg);
    }

    public UnknownException(Throwable nestedThrowable) {
        super(nestedThrowable);
    }

    public UnknownException(String msg, Throwable nestedThrowable) {
        super(msg, nestedThrowable);
    }
}
