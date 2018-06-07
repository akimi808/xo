package com.akimi808.xo.server;


import java.util.HashMap;
import java.util.Random;

import com.akimi808.xo.common.Mark;

/**
 * Created by akimi808 on 25/12/2017.
 */
public class Game {
    private Player firstPlayer;
    private Player secondPlayer;
    public Mark turn = Mark.X;
    public HashMap<Integer, Mark> field = new HashMap<>();

    public Game(Player firstPlayer) {
       this.firstPlayer = firstPlayer;
        Mark type = Mark.valueOf(new Random().nextInt(2));
        firstPlayer.setType(type);


    }

    public void addSecondPlayer(Player player) {
        this.secondPlayer = player;
        secondPlayer.setType(firstPlayer.getType().opposite());
    }

    public boolean hasSecondPlayer() {
        return secondPlayer != null;
    }

    public String getSecondPlayersName() {
        return secondPlayer.getName();
    }

    public Player choseXplayer() {
        if (firstPlayer.getType().equals(Mark.X)) {
            return firstPlayer;
        } else {
            return secondPlayer;
        }
    }

    public boolean isPlayersMove(Player player) {
        return turn.equals(player.getType());
    }

    public MoveResult processMove(int move, Player player) {
        synchronized (this) {
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
    }

    public GameState getGameState() {
        synchronized (this) {
            for (int i = 1; i <= 7; i += 3) {
                if (field.get(i) != null && field.get(i) == field.get(i + 1) && field.get(i + 1) == field.get(i + 2)) {
                    return GameState.WON;
                }
            }
            for (int i = 1; i <= 3; i += 1) {
                if (field.get(i) != null && field.get(i) == field.get(i + 3) && field.get(i + 3) == field.get(i + 6)) {
                    return GameState.WON;
                }
            }
            if (field.get(1) != null && field.get(1) == field.get(5) && field.get(5) == field.get(9)) {
                return GameState.WON;
            }
            if (field.get(3) != null && field.get(3) == field.get(5) && field.get(5) == field.get(7)) {
                return GameState.WON;
            }
            return field.size() == 9 ? GameState.DRAW : GameState.CONTINUE;
        }
    }

    private boolean isMovePosible(int move) {
        return !(move < 1 || move > 9 || field.containsKey(move));
    }

    public String getAnotherPlayersName(Player another) {
        if (firstPlayer.equals(another)) {
           return secondPlayer.getName();
        } else {
            return firstPlayer.getName();
        }
    }

    public Mark getTurnMark() {
        return turn;
    }
}
