package com.akimi808.xo.common;

import com.akimi808.xo.server.Client;

import java.nio.ByteBuffer;

/**
 * @author Andrey Larionov
 */
public class Update extends Message {
    public static final byte TYPE = 3;

    @Override
    public void handle(Handler handler, Client client) {
        handler.handleUpdate(this, client);
    }

    @Override
    protected byte getType() {
        return 0;
    }

    @Override
    protected short getBodySize() {
        return 0;
    }

    @Override
    protected void writeBody(ByteBuffer buffer) {

    }

}
