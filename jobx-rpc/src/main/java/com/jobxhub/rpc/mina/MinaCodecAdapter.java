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
package com.jobxhub.rpc.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.*;
import com.jobxhub.common.Constants;
import com.jobxhub.common.ext.ExtensionLoader;
import com.jobxhub.common.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MinaCodecAdapter implements ProtocolCodecFactory {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Serializer serializer = ExtensionLoader.load(Serializer.class);

    private Class<?> encodeClass;

    private Class<?> decodeClass;

    public MinaCodecAdapter(Class<?> encodeClass, Class<?> decodeClass) {
        this.encodeClass = encodeClass;
        this.decodeClass = decodeClass;
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return new MinaEncoder(this.encodeClass);
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return new MinaDecoder(this.decodeClass);
    }

    final class MinaDecoder<T> extends CumulativeProtocolDecoder {

        private Class<T> type;

        public MinaDecoder(Class<T> type) {
            this.type = type;
        }

        @Override
        public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
            if (in.limit() <= 0 || in.remaining() < Constants.HEADER_SIZE) {
                return false;
            }
            in.mark();
            int dataLength = in.getInt();

            if (in.remaining() < dataLength) {
                //logger.warn("[JobX]serializer error!body length < {}", dataLength);
                in.reset();
                return false;
            }
            byte[] data = new byte[dataLength];
            in.get(data);
            Object obj =  serializer.deserialize(data,type);
            out.write(obj);
            return true;
        }
    }

    final class MinaEncoder<T> implements ProtocolEncoder {

        private Class<T> type;

        public MinaEncoder(Class<T> type) {
            this.type = type;
        }

        @Override
        public void encode(IoSession session, Object msg, ProtocolEncoderOutput out) throws Exception {
            if (type.isInstance(msg)) {
                byte[] data =  serializer.serialize(msg);
                IoBuffer buffer = IoBuffer.allocate(100);
                buffer.setAutoExpand(true);
                buffer.setAutoShrink(true);
                buffer.putInt(data.length);
                buffer.put(data);
                buffer.flip();
                session.write(buffer);
            }
        }

        @Override
        public void dispose(IoSession session) throws Exception {

        }

    }


}
