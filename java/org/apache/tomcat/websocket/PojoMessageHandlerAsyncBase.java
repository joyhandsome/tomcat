/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.websocket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import javax.websocket.MessageHandler;
import javax.websocket.Session;

public abstract class PojoMessageHandlerAsyncBase<T>
        extends PojoMessageHandlerBase<T> implements MessageHandler.Async<T> {

    private final int indexBoolean;

    public PojoMessageHandlerAsyncBase(Object pojo, Method method,
            Session session, Object[] params, int indexPayload,
            boolean wrap, int indexBoolean, int indexSession) {
        super(pojo, method, session, params, indexPayload, wrap,
                indexSession);
        this.indexBoolean = indexBoolean;
    }


    @Override
    public void onMessage(T message, boolean last) {
        Object[] parameters = params.clone();
        if (indexBoolean != -1) {
            parameters[indexBoolean] = Boolean.valueOf(last);
        }
        if (indexSession != -1) {
            parameters[indexSession] = session;
        }
        if (unwrap) {
            parameters[indexPayload] = ((ByteBuffer) message).array();
        } else {
            parameters[indexPayload] = message;
        }
        Object result;
        try {
            result = method.invoke(pojo, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
        processResult(result);
    }
}
