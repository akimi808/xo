package com.akimi808.xo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by akimi808 on 12/11/2017.
 */
public class ServerProtocol {
    private static final Logger log = LogManager.getLogger(ServerProtocol.class);
    public ServerState serverState = ServerState.INTRO;
    public final XoServer xoServer;
    private Game game;
    private Player player;
    ArrayList<Message>listOfMessages = new ArrayList<>();

    public ServerProtocol(XoServer xoServer) {
        this.xoServer = xoServer;
    }


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
                    player = new Player(name);
                    game = xoServer.registerNewPlayer(player);
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

    private int extractPlayersMove(String line) {
        return Integer.parseInt(line.substring("My move ".length() + 1));
    }


    private String extractPlayerName(String message) {
        return message.substring("Player's name".length() + 1);
    }

    public ServerState getServerState() {
        return serverState;
    }

    public ArrayList<Message> decodeMessage(ByteBuffer readBytes) {
        byte[] incompleteMessage = new byte[readBytes.remaining()];
        String completeMessageStr = "";
        String incompleteMessageStr = "";
        int i = 0;
        try {
            while (readBytes.hasRemaining()) {
                byte b = readBytes.get();
                if (b != '\n') {
                    incompleteMessage[i] = b;
                    i++;
                } else {
                    if (listOfMessages.size() == 0 || listOfMessages.get(listOfMessages.size() - 1).isComplete()) {
                        completeMessageStr = new String(incompleteMessage, 0, i, "ISO-8859-1");
                        listOfMessages.add(new Message(true, completeMessageStr));
                    } else {
                        Message partOfMessage = listOfMessages.get(listOfMessages.size() - 1);
                        String secondPart = new String(incompleteMessage, 0, i, "ISO-8859-1");
                        partOfMessage.setText(partOfMessage.getText() + secondPart);
                        partOfMessage.setComplete(true);
                       // listOfMessages.add(partOfMessage);
                    }
                    incompleteMessage = new byte[readBytes.remaining()];
                    i = 0;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (i != 0) {
            try {
                incompleteMessageStr = new String(incompleteMessage, 0, i, "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            listOfMessages.add(new Message(false, incompleteMessageStr));
        }

        return listOfMessages;
    }
}

