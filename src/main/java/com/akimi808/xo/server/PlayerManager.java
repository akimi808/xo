package com.akimi808.xo.server;

/**
 * Created by akimi808 on 24/04/2018.
 */
public class PlayerManager {
    public static Player getPlayer(String name) {
        return new Player(name);
    }
}
