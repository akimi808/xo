package com.akimi808.xo.server;

import java.nio.channels.SocketChannel;

/**
 * Created by akimi808 on 06/03/2018.
 */
public class Client {
    private SocketChannel socketChannel;
    private MessageReader messageReader = new MessageReader();
    private ServerProtocol serverProtocol;
    private MessageWriter messageWriter = new MessageWriter();


    public Client(SocketChannel socketChannel, ServerProtocol serverProtocol) {
        this.socketChannel = socketChannel;
        this.serverProtocol = serverProtocol;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public MessageReader getMessageReader() {
        return messageReader;
    }

    public ServerProtocol getServerProtocol() {
        return serverProtocol;
    }

    public MessageWriter getMessageWriter() {
        return messageWriter;
    }
}
