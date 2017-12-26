package com.akimi808.xo.server;

/**
 * Created by akimi808 on 25/12/2017.
 */
public class Player {
    private String name;
    private Figure type;

    public Player(String name) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setType(Figure type) {
        this.type = type;
    }

    public Figure getType() {
        return type;
    }
}
