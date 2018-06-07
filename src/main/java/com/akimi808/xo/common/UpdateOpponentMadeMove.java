package com.akimi808.xo.common;

/**
 * @author Andrey Larionov
 */
public class UpdateOpponentMadeMove extends Update {
    public UpdateOpponentMadeMove(Integer sessionId) {
        super(sessionId);
    }

    public Mark getMark() {
        return null;
    }

    public Integer getPlacedPosition() {
        return null;
    }
}
