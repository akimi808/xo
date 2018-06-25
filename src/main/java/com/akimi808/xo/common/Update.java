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

}
