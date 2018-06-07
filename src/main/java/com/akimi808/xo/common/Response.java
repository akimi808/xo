package com.akimi808.xo.common;

import com.akimi808.xo.server.Client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.nio.ByteBuffer;

/**
 * @author Andrey Larionov
 */
public class Response extends Message {
    private static final Set<Class<?>> valueTypes = new HashSet<>(Arrays.asList(Byte.class, Integer.class, Boolean.class, String.class));

    public static final byte TYPE = 2;
    private final Integer requestId;
    private ResponseType responseType;
    private final String responseClassName;
    private final Type[] parameterTypes;
    private final Object[] parameterValues;


    public Response(Integer sessionId, Integer requestId, Object responseObject) {
        super(sessionId);
        this.requestId = requestId;
        Class<?> clazz = responseObject.getClass();
        responseClassName = clazz.getName();
        if (valueTypes.contains(clazz)) {
            responseType = ResponseType.VALUE;
            parameterTypes = new Type[] {Type.fromClass(clazz)};
            parameterValues = new Object[]{responseObject};
        } else if (responseObject instanceof Throwable) {
            responseType = ResponseType.EXCEPTION;
            parameterTypes = new Type[] {Type.STRING};
            parameterValues = new Object[] {((Throwable) responseObject).getMessage()};
        } else if (responseObject instanceof Serializable) {
            responseType = ResponseType.OBJECT;
            Object[] params = ((Serializable) responseObject).getParams();
            parameterTypes = new Type[params.length];
            parameterValues = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                parameterTypes[i] = Type.fromClass(param.getClass());
                parameterValues[i] = param;
            }
        } else {
            throw new RuntimeException();
        }
    }

    public Response(Integer sessionId, Integer requestId, ResponseType responseType, String responseClassName,
            Type[] parameterTypes, Object[] parameterValues)
    {
        super(sessionId);
        this.requestId = requestId;
        this.responseType = responseType;
        this.responseClassName = responseClassName;
        this.parameterTypes = parameterTypes;
        this.parameterValues = parameterValues;
    }

    @Override
    public void handle(Handler handler) {
        handler.handleResponse(this);
    }

    public Integer getRequestId() {
        return requestId;
    }

    public Object getResponseObject() {
        return deserializResponseObject(getObjectClass());
    }

    public Class<?>[] getParameterClasses() {
        return Arrays.stream(parameterTypes).map(Type::toClass).toArray(size -> new Class<?>[size]);
    }

    public Object[] getParameters() {
        return parameterValues;
    }

    public Class<?> getObjectClass() {
        try {
            return Class.forName(responseClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException();
        }
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    @Override
    protected byte getType() {
        return TYPE;
    }

    public static Message read(Integer sessionId, ByteBuffer buffer) {
        Integer responseId = readResponseId(buffer);
        ResponseType responseType = readResponseValue(buffer);
        String responseClassName = readString(buffer);
        final Type[] parameterTypes = readParameterTypes(buffer);
        final Object[] parameterValues = readParameterValues(buffer, parameterTypes);
        return new Response(sessionId, responseId, responseType, responseClassName, parameterTypes, parameterValues);
    }

    private static ResponseType readResponseValue(ByteBuffer buffer) {
        byte repr = readByte(buffer);
        return ResponseType.valueOf(repr);
    }

    private static Integer readResponseId(ByteBuffer buffer) {
        return readInteger(buffer);
    }

    @Override
    protected short getBodySize() {
        short idSizeInBytes = 4;
        short typeSizeInBytes = 1;
        short classNameSizeInBytes = (short) responseClassName.getBytes().length;
        short parametersLenInBytes = (short) (2 + parameterTypes.length); // ?

        short parametersSize = 0;
        for (int i = 0; i < parameterTypes.length; i++) {
            Object value = parameterValues[i];
            Type type = parameterTypes[i];
            parametersSize += type.getValueSize(value);
        }

        return (short) (idSizeInBytes + typeSizeInBytes + classNameSizeInBytes + parametersLenInBytes + parametersSize);
    }

    @Override
    protected void writeBody(ByteBuffer buffer) {
        buffer.putInt(requestId);
        buffer.put((byte) responseType.ordinal());
        writeString(responseClassName, buffer);
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

    private Object deserializResponseObject(Class<?> clazz) {
        try {
            if (!this.getObjectClass().equals(clazz)) {
                throw new RuntimeException();
            }
            switch (this.getResponseType()) {
                case VALUE:
                    return clazz.cast(this.getParameters()[0]);
                case OBJECT:
                    Constructor<?> objCons = clazz.getConstructor(this.getParameterClasses());
                    return objCons.newInstance(this.getParameters());
                case EXCEPTION:
                    Constructor<?> excCons = this.getObjectClass().getConstructor(this.getParameterClasses());
                    throw (RuntimeException) excCons.newInstance(this.getParameters());
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
