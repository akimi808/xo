package com.akimi808.xo.client;

import com.akimi808.xo.common.Mark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Andrey Larionov
 */
public class Game {
    private final Integer gameId;
    private final Mark mark;
    private Mark markToMove = Mark.X;
    private final String opponentName;
    private final Mark[] field = new Mark[9];
    private boolean started;
    private boolean finished;

    public Game(Integer gameId, Mark mark, String opponentName) {
        this.gameId = gameId;
        this.mark = mark;
        this.opponentName = opponentName;
        this.started = false;
        this.finished = false;
    }

    public Integer getGameId() {
        return gameId;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isMyTurn() {
        return markToMove.equals(mark);
    }

    public void placeMark(Mark mark, Integer placedPosition) {
        field[placedPosition] = mark;
        markToMove = mark.opposite();
    }

    public List<Integer> getAvailableMoves() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < field.length; i++) {
            result.add(i);
        }
        return result;
    }

    public boolean isStarted() {
        return started;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setTurnMark(Mark turnMark) {
        markToMove = turnMark;
    }
}
