package com.akimi808.xo.server;

import com.akimi808.xo.client.XoClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by akimi808 on 12/11/2017.
 */
public class ServerProtocol {
    private static final Logger log = LogManager.getLogger(ServerProtocol.class);
    public ServerState serverState = ServerState.INIT;



    public String processMessage(String line) {
        if ("Client/XO game".equals(line)) {
            return "Server/XO game";
        } else if (line.startsWith("Player's name")) {
            return "No players";
        } else if ("Bye".equals(line)) {
            serverState = ServerState.TERMINATE;
            return "Bye";
        }
        return "Bye";
    }

    public ServerState getServerState() {
        return serverState;
    }
}

