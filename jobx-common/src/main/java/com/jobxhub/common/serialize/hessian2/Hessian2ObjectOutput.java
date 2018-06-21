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


import com.caucho.hessian.io.Hessian2Output;
import com.jobxhub.common.serialize.ObjectOutput;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Hessian2 Object output.
 */
public class Hessian2ObjectOutput implements ObjectOutput {

    private final Hessian2Output hessian2Output;

    public Hessian2ObjectOutput(OutputStream os) {
        hessian2Output = new Hessian2Output(os);
        hessian2Output.setSerializerFactory(Hessian2SerializerFactory.SERIALIZER_FACTORY);
    }

    public void writeBool(boolean v) throws IOException {
        hessian2Output.writeBoolean(v);
    }

    public void writeByte(byte v) throws IOException {
        hessian2Output.writeInt(v);
    }

    public void writeShort(short v) throws IOException {
        hessian2Output.writeInt(v);
    }

    public void writeInt(int v) throws IOException {
        hessian2Output.writeInt(v);
    }

    public void writeLong(long v) throws IOException {
        hessian2Output.writeLong(v);
    }

    public void writeFloat(float v) throws IOException {
        hessian2Output.writeDouble(v);
    }

    public void writeDouble(double v) throws IOException {
        hessian2Output.writeDouble(v);
    }

    public void writeBytes(byte[] b) throws IOException {
        hessian2Output.writeBytes(b);
    }

    public void writeBytes(byte[] b, int off, int len) throws IOException {
        hessian2Output.writeBytes(b, off, len);
    }

    public void writeUTF(String v) throws IOException {
        hessian2Output.writeString(v);
    }

    public void writeObject(Object obj) throws IOException {
        hessian2Output.writeObject(obj);
    }

    public void flushBuffer() throws IOException {
        hessian2Output.flushBuffer();
    }
}