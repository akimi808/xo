package com.akimi808.xo.common;

import java.util.HashMap;
import java.util.Map;

import java.nio.ByteBuffer;

/**
 * @author Andrey Larionov
 */
public enum Type {
    BYTE,
    INTEGER,
    STRING,
    BOOLEAN,
    ;

    private static final Map<Class<?>, Type> classesToTypes = new HashMap<>();
    static {
        classesToTypes.put(String.class, Type.STRING);
        classesToTypes.put(Byte.class, Type.BYTE);
        classesToTypes.put(Integer.class, Type.INTEGER);
        classesToTypes.put(Boolean.class, Type.BOOLEAN);
    }

    public static Type valueOf(byte repr) {
        for (Type type : Type.values()) {
            if (type.ordinal() == repr) {
                return type;
            }
        }
        throw new RuntimeException();
    }

    public Object readValue(ByteBuffer buffer) {
        switch (this) {
        case BYTE:
            return Message.readByte(buffer);
        case STRING:
            return Message.readString(buffer);
        case INTEGER:
            return Message.readInteger(buffer);
        case BOOLEAN:
            return Message.readByte(buffer) > 0;
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
        case BOOLEAN:
            return Boolean.class;
        }
        throw new RuntimeException();
    }

    public static Type fromClass(Class<?> clazz) {
        if (classesToTypes.containsKey(clazz)) {
            return classesToTypes.get(clazz);
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
