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
import com.esotericsoftware.kryo.io.Output;
import com.jobxhub.common.serialize.Cleanable;
import com.jobxhub.common.serialize.ObjectOutput;
import com.jobxhub.common.serialize.kryo.utils.KryoUtils;

import java.io.IOException;
import java.io.OutputStream;

public class KryoObjectOutput implements ObjectOutput, Cleanable {

    private Output output;
    private Kryo kryo;

    public KryoObjectOutput(OutputStream outputStream) {
        output = new Output(outputStream);
        this.kryo = KryoUtils.get();
    }

    public void writeBool(boolean v) throws IOException {
        output.writeBoolean(v);
    }

    public void writeByte(byte v) throws IOException {
        output.writeByte(v);
    }

    public void writeShort(short v) throws IOException {
        output.writeShort(v);
    }

    public void writeInt(int v) throws IOException {
        output.writeInt(v);
    }

    public void writeLong(long v) throws IOException {
        output.writeLong(v);
    }

    public void writeFloat(float v) throws IOException {
        output.writeFloat(v);
    }

    public void writeDouble(double v) throws IOException {
        output.writeDouble(v);
    }

    public void writeBytes(byte[] v) throws IOException {
        if (v == null) {
            output.writeInt(-1);
        } else {
            writeBytes(v, 0, v.length);
        }
    }

    public void writeBytes(byte[] v, int off, int len) throws IOException {
        if (v == null) {
            output.writeInt(-1);
        } else {
            output.writeInt(len);
            output.write(v, off, len);
        }
    }


    public void writeUTF(String v) throws IOException {
        output.writeString(v);
    }

    public void writeObject(Object v) throws IOException {
        // TODO carries class info every time.
        kryo.writeClassAndObject(output, v);
    }

    public void flushBuffer() throws IOException {
        output.flush();
    }

    public void cleanup() {
        KryoUtils.release(kryo);
        kryo = null;
    }
}