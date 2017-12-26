package com.akimi808.xo.server;

import org.omg.CORBA.DynAnyPackage.Invalid;

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
            //TODO: process move
            field.put(move, player.getType());
            turn = turn.opposite();
            // FIXME
            return MoveResult.CHANGE_TURN;
        } else {
            return MoveResult.INVALID_MOVE;
        }
    }

    private boolean isMovePosible(int move) {
        return !(move < 1 || move > 9 || field.containsKey(move));
    }
}
