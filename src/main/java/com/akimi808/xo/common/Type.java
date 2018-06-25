package com.akimi808.xo.common;

import java.nio.ByteBuffer;

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

    public short getValueSize(Object value) {
        switch (this) {
            case BYTE:
                return 1;
            case INTEGER:
                return 4;
            case STRING:
                String stringValue = (String) value;
                return (short) (2 + stringValue.getBytes().length);
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

    public void writeValue(Object value, ByteBuffer buffer) {
        switch (this) {
            case BYTE:
                buffer.put((byte)value);
                break;
            case INTEGER:
                buffer.putInt((Integer) value);
                break;
            case STRING:
                Message.writeString((String)value, buffer);
                break;
        }
    }
}
