package com.akimi808.xo.common;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.akimi808.xo.server.Client;

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

    public static Message read(ByteBuffer buffer) {
        Integer requestId = readRequestId(buffer);
        String methodName = readMethodName(buffer);
        Type[] parameterTypes = readParameterTypes(buffer);
        Object[] parameterValues = readParameterValues(buffer, parameterTypes);
        return new Request(requestId, methodName, parameterTypes, parameterValues);
    }

    @Override
    public void handle(Handler handler, Client client) {
        handler.handleRequest(this, client);
    }

    @Override
    protected byte getType() {
        return this.TYPE;
    }

    @Override
    protected short getBodySize() {
        short requestIdInBytes = 4;
        short methodLenInBytes = 2;
        short methodInBytes = (short) methodName.getBytes().length;
        short parametersLenInBytes = (short) (2 + parameterTypes.length); // ?

        short parametersSize = 0;
        for (int i = 0; i < parameterTypes.length; i++) {
            Object value = parameterValues[i];
            Type type = parameterTypes[i];
            parametersSize += type.getValueSize(value);
        }
        return (short) (requestIdInBytes + methodLenInBytes + methodInBytes + parametersLenInBytes + parametersSize);
    }


    @Override
    protected void writeBody(ByteBuffer buffer) {
        buffer.putInt(requestId);
        writeMethodName(buffer);
        buffer.putShort((short) parameterTypes.length);
        for (Type type : parameterTypes) {
            buffer.put((byte) type.ordinal());
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            Type type = parameterTypes[i];
            Object value = parameterValues[i];
            type.writeValue(value, buffer);
        }
    }

    private void writeMethodName(ByteBuffer buffer) {
        Message.writeString(methodName, buffer);
    }

    private static Object[] readParameterValues(ByteBuffer buffer, Type[] parameterTypes) {
        Object[] values = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Type type = parameterTypes[i];
            values[i] = type.readValue(buffer);
        }
        return values;
    }

    private static Type[] readParameterTypes(ByteBuffer buffer) {
        short parametersLen = readShort(buffer);
        Type[] types = new Type[parametersLen];
        for (short i = 0; i < parametersLen; i++) {
            byte repr = readByte(buffer);
            types[i] = Type.valueOf(repr);
        }
        return types;
    }

    private static Integer readRequestId(ByteBuffer buffer) {
        return readInteger(buffer);
    }

    private static String readMethodName(ByteBuffer buffer) {
        return readString(buffer);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        if (requestId != null ? !requestId.equals(request.requestId) : request.requestId != null) return false;
        if (methodName != null ? !methodName.equals(request.methodName) : request.methodName != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(parameterTypes, request.parameterTypes)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(parameterValues, request.parameterValues);
    }

    @Override
    public int hashCode() {
        int result = requestId != null ? requestId.hashCode() : 0;
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(parameterTypes);
        result = 31 * result + Arrays.hashCode(parameterValues);
        return result;
    }
}
