package com.akimi808.xo.common;

public enum ResponseType {
    OBJECT,
    VALUE,
    EXCEPTION;

    public static ResponseType valueOf(byte repr) {
        for (ResponseType type : ResponseType.values()) {
            if (type.ordinal() == repr) {
                return type;
            }
        }
        throw new RuntimeException();
    }
}
