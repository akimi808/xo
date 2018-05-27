package com.akimi808.xo.common;

import com.akimi808.xo.server.Client;

public interface Handler {
    void handleRequest(Request request, Client client);
    void handleResponse(Response response, Client client);
    void handleUpdate(Update update, Client client);
}
