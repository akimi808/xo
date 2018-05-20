package com.akimi808.xo.common;

import com.akimi808.xo.server.Client;
import com.akimi808.xo.server.SocketProcessor;

/**
 * @author Andrey Larionov
 */
public class Update extends Message {
    public static final byte TYPE = 3;

    @Override
    public void handle(SocketProcessor socketProcessor, Client client) {
        socketProcessor.handleUpdate(this, client);
    }
}
