package com.akimi808.xo.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akimi808 on 24/04/2018.
 */
public class GameManager {
    private List<Game> gameList = new ArrayList<>();


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
