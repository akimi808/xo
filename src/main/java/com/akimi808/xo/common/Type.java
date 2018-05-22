package com.akimi808.xo.common;

/**
 * @author Andrey Larionov
 */
public enum Type {
    BYTE,
    INTEGER,
    STRING
    ;

    public static Type valueOf(byte repr) {
        for (Type type : Type.values()) {
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
        case INTEGER:
            return Message.readInteger(buffer);
        }
        throw new RuntimeException();
    }

    public Class<?> toClass() {
        switch (this) {
        case BYTE:
            return Byte.class;
        case STRING:
            return String.class;
        case INTEGER:
            return Integer.class;
        }
        throw new RuntimeException();
    }
}
