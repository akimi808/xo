package com.akimi808.xo.common;

/**
 * @author Andrey Larionov
 */
public enum Types {
    BYTE,
    INTEGER,
    STRING
    ;

    public static Types valueOf(byte repr) {
        for (Types type : Types.values()) {
            if (type.ordinal() == repr) {
                return type;
            }
        }
        throw new RuntimeException();
    }

    public Object readValue(RingBuffer buffer) {
        switch (this) {
        case BYTE:
            return Message.readByte(buffer);
        case STRING:
            return Message.readString(buffer);
        }
        return null;
    }
}
