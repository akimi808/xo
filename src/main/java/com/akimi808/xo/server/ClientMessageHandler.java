package com.akimi808.xo.server;

import com.akimi808.xo.common.Handler;
import com.akimi808.xo.common.Request;
import com.akimi808.xo.common.Response;
import com.akimi808.xo.common.Update;

public class ClientMessageHandler implements Handler {
    private final Client client;
    private final SocketProcessor handler;

    public ClientMessageHandler(Client client, SocketProcessor socketProcessor) {
        this.client = client;
        this.handler = socketProcessor;
    }

    @Override
    public void handleRequest(Request request) {
        handler.handleRequest(request, client);
    }

    @Override
    public void handleResponse(Response response) {
        handler.handleResponse(response, client);
    }

    @Override
    public void handleUpdate(Update update) {
        handler.handleUpdate(update, client);
    }
}
