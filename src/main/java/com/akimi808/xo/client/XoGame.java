package com.akimi808.xo.client;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.akimi808.xo.common.*;

/**
 * @author Andrey Larionov
 */
public class XoGame {
    private Game game;

    public static void main(String[] args) {
        new XoGame().run();
    }

    private void run() {
        // Creating client
        String name = "";
        final XoClient2 client = new XoClient2();
        try {
            // Establishing connection and login
            // in try catch block, cause fail is a fatal error
            client.connect();
            client.login(name);
        } catch (ConnectionException e) {

        } catch (LoginException e) {

        }

        while (true) {
            List<Update> updates = client.getUpdates().stream().filter(u -> u instanceof UpdateGameStarted).collect(Collectors.toList());
            if (!updates.isEmpty()) {
                UpdateGameStarted gameUpdate = (UpdateGameStarted) updates.get(0);
                game = new Game(gameUpdate.getGameId(), gameUpdate.getMark(), gameUpdate.getOpponentName());
                break;
            }
        }

        while (!game.isFinished()) {
            if (!game.isMyTurn()) {
                while (true) {
                    List<Update> updates = client.getUpdates().stream().filter(u -> u instanceof UpdateOpponentMadeMove).collect(Collectors.toList());
                    if (!updates.isEmpty()) {
                        UpdateOpponentMadeMove moveUpdate = (UpdateOpponentMadeMove) updates.get(0);
                        game.doMove(moveUpdate.getMark(), moveUpdate.getPlacedPosition());
                        break;
                    }
                }
            }
            List<Integer> availableMoves = game.getAvailableMoves();
            Integer selectedMove = availableMoves.get(new Random().nextInt(availableMoves.size()));
            Response response = client.makeMove(game.getGameId(), selectedMove);
        }

        while (true) {
            List<Update> updates = client.getUpdates().stream().filter(u -> u instanceof UpdateGameFinished).collect(Collectors.toList());
            if (!updates.isEmpty()) {
                UpdateGameFinished gameUpdate = (UpdateGameFinished) updates.get(0);
                System.out.println(String.format("%s %s %s", gameUpdate.getGameId(), gameUpdate.getWinnerMark(), gameUpdate.getWinnerName()));
                break;
            }
        }

        client.disconnect();
    }
}
