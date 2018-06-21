/**
 * Copyright 2016 The jobx Project
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


package com.jobxhub.common.logging;

/**
 * Holds the results of formatting done by {@link MessageFormatter}.
 * <p>
 * Forked from <a href="https://github.com/netty/netty">Netty</a>.
 */
class FormattingTuple {

    private final String message;
    private final Throwable throwable;

    FormattingTuple(String message) {
        this(message, null);
    }

    FormattingTuple(String message, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
    }

    static Object[] trimmedCopy(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            throw new IllegalStateException("empty or null argument array");
        }
        final int trimmedLen = argArray.length - 1;
        Object[] trimmed = new Object[trimmedLen];
        System.arraycopy(argArray, 0, trimmed, 0, trimmedLen);
        return trimmed;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
