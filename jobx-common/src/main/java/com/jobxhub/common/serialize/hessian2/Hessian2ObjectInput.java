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
package com.jobxhub.common.serialize.hessian2;

import com.caucho.hessian.io.Hessian2Input;
import com.jobxhub.common.serialize.ObjectInput;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Hessian2 Object input.
 */
public class Hessian2ObjectInput implements ObjectInput {

    private final Hessian2Input hessian2Input;

    public Hessian2ObjectInput(InputStream is) {
        hessian2Input = new Hessian2Input(is);
        hessian2Input.setSerializerFactory(Hessian2SerializerFactory.SERIALIZER_FACTORY);
    }

    public boolean readBool() throws IOException {
        return hessian2Input.readBoolean();
    }

    public byte readByte() throws IOException {
        return (byte) hessian2Input.readInt();
    }

    public short readShort() throws IOException {
        return (short) hessian2Input.readInt();
    }

    public int readInt() throws IOException {
        return hessian2Input.readInt();
    }

    public long readLong() throws IOException {
        return hessian2Input.readLong();
    }

    public float readFloat() throws IOException {
        return (float) hessian2Input.readDouble();
    }

    public double readDouble() throws IOException {
        return hessian2Input.readDouble();
    }

    public byte[] readBytes() throws IOException {
        return hessian2Input.readBytes();
    }

    public String readUTF() throws IOException {
        return hessian2Input.readString();
    }

    public Object readObject() throws IOException {
        return hessian2Input.readObject();
    }

    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> cls) throws IOException,
            ClassNotFoundException {
        return (T) hessian2Input.readObject(cls);
    }

    public <T> T readObject(Class<T> cls, Type type) throws IOException, ClassNotFoundException {
        return readObject(cls);
    }

}