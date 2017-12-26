package com.akimi808.xo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by akimi808 on 12/11/2017.
 */
public class XoServer {
    private static final Logger log = LogManager.getLogger(XoServer.class);
    private List<Game> gameList;

    public static void main(String[] args) {
        new XoServer().run();
    }

    private void run() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(2810);
            log.debug("Socket created");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // do job
        try {
            while (true) {
                Socket socket1 = socket.accept();
                new ClientHandler(socket1, this).start(); //ClientThread
                log.debug("Thread created");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public Game registerNewPlayer(Player player) {
        for (Game game : gameList) {
            if (!game.hasSecondPlayer()) {
                game.addSecondPlayer(player);
                return game;
            }
        }
        Game game = new Game(player);
        gameList.add(game);
        return game;
    }

    public List<Game> getGameList() {
        return gameList;
    }

}
