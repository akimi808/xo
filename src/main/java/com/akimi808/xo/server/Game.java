package com.akimi808.xo.server;


import java.util.HashMap;
import java.util.Random;

/**
 * Created by akimi808 on 25/12/2017.
 */
public class Game {
    private Player firstPlayer;
    private Player secondPlayer;
    public Figure turn = Figure.X;
    public HashMap<Integer, Figure> field = new HashMap<>();

    public Game(Player firstPlayer) {
       this.firstPlayer = firstPlayer;
    }

    public void addSecondPlayer(Player player) {
        this.secondPlayer = player;
    }

    public boolean hasSecondPlayer() {
        return secondPlayer != null;
    }

    public String getSecondPlayersName() {
        return secondPlayer.getName();
    }

    public Player choseXplayer() {
        Figure type = Figure.valueOf(new Random().nextInt(2));
        firstPlayer.setType(type);
        secondPlayer.setType(type.opposite());
        if (type.equals(Figure.X)) {
            return firstPlayer;
        } else {
            return secondPlayer;
        }
    }

    public boolean isPlayersMove(Player player) {
        return turn.equals(player.getType());
    }

    public MoveResult processMove(int move, Player player) {
        if (isMovePosible(move)) {
            field.put(move, player.getType());
            switch (getGameState()) {
                case CONTINUE:
                    turn = turn.opposite();
                    return MoveResult.CHANGE_TURN;
                case WON:
                    return MoveResult.GAME_OVER;
                case DRAW:
                    break;
            }
            // FIXME
            return MoveResult.DRAW;
        } else {
            return MoveResult.INVALID_MOVE;
        }
    }

    public GameState getGameState() {
        for (int i = 1; i <= 7; i += 3) {
            if (field.get(i) == field.get(i + 1) & field.get(i + 1) == field.get(i + 2)) {
                return GameState.WON;
            }
        }
        for (int i = 1; i <= 3; i += 1) {
            if (field.get(i) == field.get(i + 3) & field.get(i + 3) == field.get(i + 6))
            {
                return GameState.WON;
            }
        }
        if (field.get(1) == field.get(5) & field.get(5) == field.get(9)) {
            return GameState.WON;
        }
        if (field.get(3) == field.get(5) & field.get(5) == field.get(7)) {
            return GameState.WON;
        }
        return field.size() == 9 ? GameState.DRAW : GameState.CONTINUE;
    }

    private boolean isMovePosible(int move) {
        return !(move < 1 || move > 9 || field.containsKey(move));
    }
}
