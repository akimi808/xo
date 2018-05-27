package com.akimi808.xo.common;

import com.akimi808.xo.server.Client;

/**
 * @author Andrey Larionov
 */
public class Response extends Message {
    public static final byte TYPE = 2;

    @Override
    public void handle(Handler handler, Client client) {
        handler.handleResponse(this, client);
    }
}
