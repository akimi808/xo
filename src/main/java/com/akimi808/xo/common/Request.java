package com.akimi808.xo.common;

import java.util.Arrays;

import com.akimi808.xo.server.Client;
import com.akimi808.xo.server.SocketProcessor;

/**
 * Created by akimi808 on 22/02/2018.
 */
public class Request extends Message {
    public static final byte TYPE = 1;
    private final Integer requestId;
    private final String methodName;
    private final Type[] parameterTypes;
    private final Object[] parameterValues;

    public Request(Integer requestId, String methodName, Type[] parameterTypes, Object[] parameterValues) {
        this.requestId = requestId;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameterValues = parameterValues;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParameterClasses() {
        return Arrays.stream(parameterTypes).map(Type::toClass).toArray(size -> new Class<?>[size]);
    }

    public Object[] getParameters() {
        return parameterValues;
    }

    public static Message read(RingBuffer buffer) {
        Integer requestId = readRequestId(buffer);
        String methodName = readMethodName(buffer);
        Type[] parameterTypes = readParameterTypes(buffer);
        Object[] parameterValues = readParameterValues(buffer, parameterTypes);
        return new Request(requestId, methodName, parameterTypes, parameterValues);
    }

    @Override
    public void handle(SocketProcessor socketProcessor, Client client) {
        socketProcessor.handleRequest(this, client);
    }

    private static Object[] readParameterValues(RingBuffer buffer, Type[] parameterTypes) {
        Object[] values = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Type type = parameterTypes[i];
            values[i] = type.readValue(buffer);
        }
        return values;
    }

    private static Type[] readParameterTypes(RingBuffer buffer) {
        short parametersLen = readShort(buffer);
        Type[] types = new Type[parametersLen];
        for (short i = 0; i < parametersLen; i++) {
            byte repr = readByte(buffer);
            types[i] = Type.valueOf(repr);
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
