package com.akimi808.xo.common;

import java.io.UnsupportedEncodingException;

import com.akimi808.xo.server.Client;
import com.akimi808.xo.server.SocketProcessor;

/**
 * Created by akimi808 on 22/02/2018.
 */
public class Request extends Message {
    public static final byte TYPE = 1;
    private final Integer requestId;
    private final String methodName;
    private final Types[] parameterTypes;
    private final Object[] parameterValues;

    public Request(Integer requestId, String methodName, Types[] parameterTypes, Object[] parameterValues) {
        this.requestId = requestId;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameterValues = parameterValues;
    }

    public String getMethodName() {
        return null;
    }

    public Class<?>[] getParameterTypes() {
        return new Class[0];
    }

    public Object[] getParameters() {
        return new Object[0];
    }

    public static Message read(RingBuffer buffer) {
        Integer requestId = readRequestId(buffer);
        String methodName = readMethodName(buffer);
        Types[] parameterTypes = readParameterTypes(buffer);
        Object[] parameterValues = readParameterValues(buffer, parameterTypes);
        return new Request(requestId, methodName, parameterTypes, parameterValues);
    }

    @Override
    public void handle(SocketProcessor socketProcessor, Client client) {
        socketProcessor.handleRequest(this, client);
    }

    private static Object[] readParameterValues(RingBuffer buffer, Types[] parameterTypes) {
        Object[] values = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Types type = parameterTypes[i];
            values[i] = type.readValue(buffer);
        }
        return values;
    }

    private static Types[] readParameterTypes(RingBuffer buffer) {
        short parametersLen = readShort(buffer);
        Types[] types = new Types[parametersLen];
        for (short i = 0; i < parametersLen; i++) {
            byte repr = readByte(buffer);
            types[i] = Types.valueOf(repr);
        }
        return types;
    }

    private static Integer readRequestId(RingBuffer buffer) {
        return readInteger(buffer);
    }

    private static String readMethodName(RingBuffer buffer) {
        return readString(buffer);
    }
}
