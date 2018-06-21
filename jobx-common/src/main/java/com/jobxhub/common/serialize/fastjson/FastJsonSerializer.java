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
package com.jobxhub.common.serialize.fastjson;

import com.jobxhub.common.serialize.ObjectInput;
import com.jobxhub.common.serialize.ObjectOutput;
import com.jobxhub.common.serialize.Serializer;
import com.jobxhub.common.serialize.support.AbstractSerializer;

import java.io.*;

/**
 * FastJsonSerialization
 */
public class FastJsonSerializer extends AbstractSerializer implements Serializer {

    public byte getContentTypeId() {
        return 6;
    }

    public String getContentType() {
        return "text/json";
    }

    @Override
    public byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = new FastJsonObjectOutput(outputStream);
        return super.serialize(outputStream,objectOutput,object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        ObjectInput objectInput =  new FastJsonObjectInput(new ByteArrayInputStream(bytes));
        return super.deserialize(objectInput,clazz);
    }

}