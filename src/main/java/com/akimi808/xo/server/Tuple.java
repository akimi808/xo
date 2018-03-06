package com.akimi808.xo.server;

/**
 * Created by akimi808 on 05/03/2018.
 */
public class Tuple {
    private String response;
    private Client client;

    public Tuple(String response, Client client) {
        this.response = response;
        this.client = client;
    }
}
