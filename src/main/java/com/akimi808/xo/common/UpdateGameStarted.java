package com.akimi808.xo.common;

/**
 * @author Andrey Larionov
 */
public class UpdateGameStarted extends Update {
    public UpdateGameStarted(Integer sessionId) {
        super(sessionId);
    }

    public Integer getGameId() {
        return null;
    }

    public Mark getMark() {
        return null;
    }

    public String getOpponentName() {
        return null;
    }
}
