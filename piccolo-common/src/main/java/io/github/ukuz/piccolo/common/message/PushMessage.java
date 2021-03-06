/*
 * Copyright 2019 ukuz90
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.ukuz.piccolo.common.message;

import io.github.ukuz.piccolo.api.connection.Connection;
import io.github.ukuz.piccolo.api.exchange.support.ByteBufMessage;
import static io.github.ukuz.piccolo.common.constants.CommandType.GATEWAY_PUSH;
import io.netty.buffer.ByteBuf;
import lombok.Builder;

import java.util.Arrays;

/**
 * @author ukuz90
 */
public class PushMessage extends ByteBufMessage {

    public String userId;
    public boolean broadcast;
    public byte[] content;

    public PushMessage(Connection connection) {
        super(connection, GATEWAY_PUSH.getCmd());
    }

    @Override
    protected void decodeBody0(ByteBuf buf) {
        userId = readString(buf);
        broadcast = readBoolean(buf);
        content = readBytes(buf);

    }

    @Override
    protected void encodeBody0(ByteBuf buf) {
        writeString(buf, userId);
        writeBoolean(buf, broadcast);
        writeBytes(buf, content);
    }

    public static PushMessage build(Connection connection) {
        return new PushMessage(connection);
    }

    public PushMessage content(byte[] content) {
        this.content = content;
        return this;
    }

    public PushMessage broadcast(boolean broadcast) {
        this.broadcast = broadcast;
        return this;
    }

    public PushMessage userId(String userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public String toString() {
        return "PushMessage{" +
                "userId='" + userId + '\'' +
                ", broadcast=" + broadcast +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
