package com.akimi808.xo.client;

import com.akimi808.xo.server.ServerProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by akimi808 on 21/11/2017.
 */
public class ClientProtocol {

    private static final Logger log = LogManager.getLogger(ClientProtocol.class);
    private ClientState clientState = ClientState.INIT;

    public String processMessage(String line) {
        if (line == null) {
            return "Client/XO game";
        } else if ("Server/XO game".equals(line)) {
            return "Player's name Alice";
        } else if ("No players".equals(line)) {
            clientState = ClientState.TERMINATE;
            return "Bye";
        }
        return "Bye";
    }

    public ClientState getClientState() {
        return clientState;
    }
}
