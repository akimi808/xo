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

    public Game(Integer gameId, Mark mark, String opponentName) {
        this.gameId = gameId;
        this.mark = mark;
        this.opponentName = opponentName;
    }

    public Integer getGameId() {
        return gameId;
    }

    public boolean isFinished() {
        for (int i = 1; i <= 7; i += 3) {
            if (field[i] != null && field[i] == field[i + 1] && field[i + 1] == field[i + 2]) {
                return true;
            }
        }
        for (int i = 1; i <= 3; i += 1) {
            if (field[i] != null && field[i] == field[i + 3] && field[i + 3] == field[i + 6]) {
                return true;
            }
        }
        if (field[1] != null && field[1] == field[5] && field[5] == field[9]) {
            return true;
        }
        if (field[3] != null && field[3] == field[5] && field[5] == field[7]) {
            return true;
        }
        return Arrays.stream(field).filter(Objects::nonNull).count() == 9;
    }

    public boolean isMyTurn() {
        return markToMove.equals(mark);
    }

    public void doMove(Mark mark, Integer placedPosition) {
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
}
