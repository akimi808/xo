package com.akimi808.xo.common;

import com.akimi808.xo.server.Client;

import java.nio.ByteBuffer;

import java.nio.ByteBuffer;

/**
 * @author Andrey Larionov
 */
public class Update extends Message {
    public static final byte TYPE = 3;

    public Update(Integer sessionId) {
        super(sessionId);
    }

    @Override
    public void handle(Handler handler) {
        handler.handleUpdate(this);
    }

    @Override
    protected byte getType() {
        return TYPE;
    }

    @Override
    protected short getBodySize() {
        return 0;
    }

    @Override
    protected void writeBody(ByteBuffer buffer) {

    }

    public static Message read(Integer sessionId, ByteBuffer buffer) {
        return null;
    }

}
