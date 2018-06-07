package com.akimi808.xo.common;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.akimi808.xo.server.Client;
import com.akimi808.xo.server.SocketProcessor;

/**
 * @author Andrey Larionov
 */
public abstract class Message {
    public static boolean hasComplete(RingBuffer buffer) {
        boolean hasComplete = false;
        buffer.mark();
        short messageLen = readShort(buffer);
        hasComplete = buffer.available() < messageLen;
        buffer.reset();
        return hasComplete;
    }

    protected static short readShort(RingBuffer buffer) {
        final byte low = buffer.take();
        final byte high = buffer.take();
        return (short) ((low & 0xFF) | ((high & 0xFF) << 8));
    }

    public static Message read(RingBuffer buffer) {
        short messageLen = readShort(buffer);
        byte messageType = readByte(buffer);
        return readMessageByType(messageType, buffer);
    }

    private static Message readMessageByType(byte messageType, RingBuffer buffer) {
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

    public static byte readByte(RingBuffer buffer) {
        return buffer.take();
    }

    public static String readString(RingBuffer buffer) {
        short stringLen = readShort(buffer);
        byte[] stringBytes = new byte[stringLen];
        buffer.take(stringBytes, stringLen);
        String string = null;
        try {
            string = new String(stringBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }

    public static Integer readInteger(RingBuffer buffer) {
        byte i1 = buffer.take();
        byte i2 = buffer.take();
        byte i3 = buffer.take();
        byte i4 = buffer.take();
        return (
                (i1 & 0xFF) |
                ((i2 & 0xFF) << 8) |
                ((i3 & 0xFF) << 16) |
                ((i4 & 0xFF) << 24));
    }

    abstract public void handle(Handler handler, Client client);

    public abstract int write(ByteBuffer writeBuffer);
}
