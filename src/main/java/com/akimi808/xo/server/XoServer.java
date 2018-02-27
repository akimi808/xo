package com.akimi808.xo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by akimi808 on 12/11/2017.
 */
public class XoServer {
    private static final Logger log = LogManager.getLogger(XoServer.class);
    private List<Game> gameList = new ArrayList<>();

    public static void main(String[] args) {
        try {
            new XoServer().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() throws IOException {
        Queue<SocketChannel> socketChannelQueue = new ArrayBlockingQueue<SocketChannel>(1000);
        new Thread(new SocketAcceptor(socketChannelQueue)).start();
        new Thread(new SocketProcessor(socketChannelQueue)).start();
    }

    public synchronized Game registerNewPlayer(Player player) {
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
