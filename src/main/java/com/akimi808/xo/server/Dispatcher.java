package com.akimi808.xo.server;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.akimi808.xo.common.Request;

/**
 * @author Andrey Larionov
 */
public class Dispatcher {
    public void dispatch(Request request, Client client) {
        try {
            final Method method = getRequestedMethod(request, client);
            method.setAccessible(true);
            method.invoke(client.getServerProtocol(), request.getParameters());
        } catch (IllegalAccessException | InvocationTargetException e) {
            sendError(client, request, e);
        } catch (NoSuchMethodException e) {
            sendNoMethodError(client, request);
        }
    }

    private Method getRequestedMethod(Request request, Client client) throws NoSuchMethodException {
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterClasses();
        return client.getServerProtocol().getClass().getDeclaredMethod(methodName, parameterTypes);
    }

    private void sendError(Client client, Request request, Exception e) {

    }

    private void sendNoMethodError(Client client, Request request) {

    }
}
