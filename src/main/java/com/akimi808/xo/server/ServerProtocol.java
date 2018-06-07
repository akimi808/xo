package com.akimi808.xo.server;

import com.akimi808.xo.common.PlayerInfo;
import com.akimi808.xo.common.UpdateGameFinished;
import com.akimi808.xo.common.UpdateGameStarted;
import com.akimi808.xo.common.UpdateGameTurnChanged;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by akimi808 on 12/11/2017.
 */
public class ServerProtocol {
    private static final Logger log = LogManager.getLogger(ServerProtocol.class);
    public ServerState serverState = ServerState.INTRO;
    private Game game;
    private Player player;
    private GameManager gameManager;

    public ServerProtocol(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public PlayerInfo login(Client client, String name) {
        game = gameManager.registerNewPlayer(new Player(name, client));
        if (game.hasSecondPlayer()) {
            client.getMessageWriter().enqueue(new UpdateGameStarted(client.getSessionId()));
        }
        return new PlayerInfo();
    }

    public Boolean makeMove(Client client, Integer gameId, Integer move) {
        MoveResult moveResult = game.processMove(move, player);
        MessageWriter messageWriter = client.getMessageWriter();
        switch (moveResult) {
            case DRAW:
            case GAME_OVER:
                messageWriter.enqueue(new UpdateGameFinished(client.getSessionId()));
                return true;
            case CHANGE_TURN:
                messageWriter.enqueue(new UpdateGameTurnChanged(client.getSessionId(), game.getTurnMark()));
                return true;
            case INVALID_MOVE:
                return false;
        }
        throw new RuntimeException();
   }

/*
    public String processMessage(String line) {
        switch (serverState) {
            case INTRO:
                if ("Client/XO game".equals(line)) {
                    serverState = ServerState.PLAYERS_LIST;
                    return "Server/XO game";
                } else {
                    return "Unexpected message";
                }
            case PLAYERS_LIST:
                if (line.startsWith("Player's name")) {
                    String name = extractPlayerName(line);
                    player = PlayerManager.getPlayer(name);
                    game = gameManager.registerNewPlayer(player); //FIXME
                    if (!game.hasSecondPlayer()) {
                        return "No players";
                    } else {
                        serverState = ServerState.GAME_STARTED;
                        return "Game started with " + game.getAnotherPlayersName(player);
                    }
                } else if (line.equals("Waiting")) {
                    if (!game.hasSecondPlayer()) {
                        return "No players";
                    } else {
                        serverState = ServerState.GAME_STARTED;
                        return "Game started with " + game.getAnotherPlayersName(player);
                    }
                } else {
                    return "Unexpected message";
                }
            case GAME_STARTED:
                if ("Ready to play".equals(line)) {
                    if (game.choseXplayer().equals(player)) {
                        serverState = ServerState.PLAY;
                        return "Your turn, your figure is X";
                    } else {
                        serverState = ServerState.PLAY;
                        return "Not your turn, your figure is O";
                    }
                } else {
                    return "Unexpected message";
                }
            case PLAY:
                if ("Waiting for".equals(line)) {
                    GameState gameState = game.getGameState();
                    if (game.isPlayersMove(player) & gameState.equals(GameState.CONTINUE)) {
                        return "Your move";
                    } else if (gameState.equals(GameState.WON)) {
                        serverState = ServerState.BYE;
                        return "You lose";
                    } else if (gameState.equals(GameState.DRAW)) {
                        serverState = ServerState.BYE;
                        return "Draw";
                    } else {
                        return "Not your turn";
                    }
                } else if (line.startsWith("My move:")) {
                    int move = extractPlayersMove(line);
                    switch (game.processMove(move, player)) {
                        case INVALID_MOVE:
                            return "Change your move";
                        case GAME_OVER:
                            serverState = ServerState.BYE;
                            return "You won!";
                        case DRAW:
                            serverState = ServerState.BYE;
                            return "Draw";
                        case CHANGE_TURN:
                            return "Not your turn";
                    }
                    return "";
                } else {
                    return "Unexpected message";
                }
            case BYE:
                if ("Bye".equals(line)) {
                    serverState = ServerState.TERMINATE;
                    return "Bye";
                } else {
                    return "Unexpected message";
                }
        }
        return null;
    }
*/

    private int extractPlayersMove(String line) {
        return Integer.parseInt(line.substring("My move ".length() + 1));
    }


    private String extractPlayerName(String message) {
        return message.substring("Player's name".length() + 1);
    }


}

