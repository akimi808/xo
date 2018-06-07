package com.akimi808.xo.common;

public class UpdateGameTurnChanged extends Update {
    private final Mark turnMark;

    public UpdateGameTurnChanged(Integer sessionId, Mark turnMark) {
        super(sessionId);
        this.turnMark = turnMark;
    }

    public Mark getTurnMark() {
        return turnMark;
    }
}
