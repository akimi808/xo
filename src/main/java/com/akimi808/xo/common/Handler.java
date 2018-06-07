package com.akimi808.xo.common;

import com.akimi808.xo.server.Client;

public interface Handler {
    void handleRequest(Request request);
    void handleResponse(Response response);
    void handleUpdate(Update update);
}
