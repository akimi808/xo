package com.akimi808.xo.common;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.akimi808.xo.server.Client;

/**
 * @author Andrey Larionov
 */
public abstract class Message {
    public static boolean hasComplete(ByteBuffer buffer) {
        boolean hasComplete = false;
        buffer.mark();
        short messageLen = readShort(buffer);
        hasComplete = buffer.limit() - buffer.position() < messageLen;
        buffer.reset();
        return hasComplete;
    }

    protected static short readShort(ByteBuffer buffer) {
        return buffer.getShort();
    }

    public static Message read(ByteBuffer buffer) {
        short messageLen = readShort(buffer);
        byte messageType = readByte(buffer);
        return readMessageByType(messageType, buffer);
    }

    private static Message readMessageByType(byte messageType, ByteBuffer buffer) {
        switch (messageType) {
            case Request.TYPE:
                return Request.read(buffer);
            case Response.TYPE:
                return Response.read(buffer);
            case Update.TYPE:
                return Update.read(buffer);
        }
        throw new RuntimeException();
    }

    public static byte readByte(ByteBuffer buffer) {
        return buffer.get();
    }

    public static String readString(ByteBuffer buffer) {
        short stringLen = readShort(buffer);
        byte[] stringBytes = new byte[stringLen];
        buffer.get(stringBytes);
        String string = null;
        try {
            string = new String(stringBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }

    public static Integer readInteger(ByteBuffer buffer) {
        byte i4 = buffer.get();
        byte i3 = buffer.get();
        byte i2 = buffer.get();
        byte i1 = buffer.get();
        return (
                (i1 & 0xFF) |
                ((i2 & 0xFF) << 8) |
                ((i3 & 0xFF) << 16) |
                ((i4 & 0xFF) << 24));
    }

    abstract public void handle(Handler handler, Client client);

    public void write(ByteBuffer buffer) {
        short len = getSizeInBytes();
        byte type = getType();
        buffer.putShort(len);
        buffer.put(type);
        writeBody(buffer);
    }

    protected abstract byte getType();


    protected short getSizeInBytes() {
        return (short) (getHeaderSize() + getBodySize());
    }

    protected abstract short getBodySize();

    private short getHeaderSize(){
        return 3;
    }


    protected abstract void writeBody(ByteBuffer buffer);

    public static void writeString(String value, ByteBuffer buffer) {
        byte[] bytes = value.getBytes();
        buffer.putShort((short) bytes.length);
        buffer.put(bytes);
    }
}
