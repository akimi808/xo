package com.akimi808.xo.common;

import com.akimi808.xo.server.Client;

import java.nio.ByteBuffer;

/**
 * @author Andrey Larionov
 */
public class Response extends Message {
    public static final byte TYPE = 2;
    private Integer responseId;
    private Type responseType;
    private Object responseValue;

    public Response(Integer responseId, Type responseType, Object responseValue) {
        this.responseId = responseId;
        this.responseType = responseType;
        this.responseValue = responseValue;
    }

    @Override
    public void handle(Handler handler, Client client) {
        handler.handleResponse(this, client);
    }

    @Override
    protected byte getType() {
        return TYPE;
    }

    public static Message read(RingBuffer buffer) {
        Integer responseId = readResponseId(buffer);
        Type responseType = readType(buffer);
        Object responseValue = readValue(buffer, responseType);
        return new Response(responseId, responseType, responseValue);
    }

    private static Object readValue(RingBuffer buffer, Type responseType) {
        return responseType.readValue(buffer);
    }

    private static Type readType(RingBuffer buffer) {
            byte repr = readByte(buffer);
            return Type.valueOf(repr);
    }

    private static Integer readResponseId(RingBuffer buffer) {
        return readInteger(buffer);
    }

    @Override
    protected short getBodySize() {
        short idSizeInBytes = 4;
        short typeSizeInBytes = 1;
        short valueSizeInBytes = responseType.getValueSize(responseValue);
        return (short) (idSizeInBytes + typeSizeInBytes + valueSizeInBytes);
    }

    @Override
    protected void writeBody(ByteBuffer buffer) {
        buffer.putInt(responseId);
        buffer.put((byte) responseType.ordinal());
        responseType.writeValue(responseValue, buffer);
    }
}
