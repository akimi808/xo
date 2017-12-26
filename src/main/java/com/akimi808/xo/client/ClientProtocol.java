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
        switch (clientState) {
            case INIT:
                clientState = ClientState.INTRO;
                return "Client/XO game";
            case INTRO:
                if ("Server/XO game".equals(line)) {
                    clientState = ClientState.PLAYER_SELECT;
                    return "Player's name Alice";
                } else {
                    return  "Unexpected message";
                }
            case PLAYER_SELECT:
                if ("No players".equals(line)) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "Waiting";
                } else if (line.startsWith("Game started with")) {
                    clientState = ClientState.GAME_STARTED;
                    return "Ready to play";
                }
                else {
                    return "Unexpected message";
                }
            case GAME_STARTED:
                if("Your turn".equals(line)) {
                    clientState = ClientState.PLAY;
                    return "My move: " + Integer.toString(1);
                } else if ("Not your turn".equals(line)) {
                    clientState = ClientState.PLAY;
                    return "Waiting for";
                } else {
                    return "Unexpected message";
                }
            case BYE:
                if ("Bye".equals(line)) {
                    clientState = ClientState.TERMINATE;
                } else  {
                    return "Unexpected message";
                }
                break;
            case TERMINATE:
                break;

        }
        return null;
    }

    public ClientState getClientState() {
        return clientState;
    }
}