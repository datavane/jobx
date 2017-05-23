/**
 * Copyright 2016 benjobs
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

package org.opencron.common.rpc.serialization;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class HessianSerializer implements Serializer {

    @Override
    public byte[] encode(Object msg) throws IOException {
        Hessian2Output out = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            out = new Hessian2Output(bos);
            out.writeObject(msg);
            out.flush();
            return bos.toByteArray();
        } finally {
            if(out!=null){
                out.close();
            }
        }
    }

    @Override
    public <T> T decode(byte[] buf, Class<T> type) throws IOException {
        Hessian2Input input = null;
        try {
            input = new Hessian2Input(new ByteArrayInputStream(buf));
            return (T) input.readObject(type);
        } finally {
            if(input!=null){
                input.close();
            }
        }
    }
}