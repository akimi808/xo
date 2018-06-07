package com.akimi808.xo.server;

import com.akimi808.xo.common.Mark;

/**
 * Created by akimi808 on 25/12/2017.
 */
public class Player {
    private String name;
    private Client client;
    private Mark type;

    public Player(String name, Client client) {
        this.name = name;
        this.client = client;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setType(Mark type) {
        this.type = type;
    }

    public Mark getType() {
        return type;
    }
}
