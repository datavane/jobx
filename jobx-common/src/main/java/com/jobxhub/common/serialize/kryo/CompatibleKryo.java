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


package com.jobxhub.common.serialize.kryo;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.jobxhub.common.util.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompatibleKryo extends Kryo {

    private static final Logger logger = LoggerFactory.getLogger(CompatibleKryo.class);

    @Override
    public Serializer getDefaultSerializer(Class type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        }

        if (!type.isArray() && !type.isEnum() && !ReflectUtils.checkZeroArgConstructor(type)) {
            if (logger.isWarnEnabled()) {
                logger.warn(type + " has no zero-arg constructor and this will affect the serialization performance");
            }
            return new JavaSerializer();
        }
        return super.getDefaultSerializer(type);
    }
}
