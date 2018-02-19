package com.akimi808.xo.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * Created by akimi808 on 19/02/2018.
 */
public class SocketAcceptor implements Runnable {
    private Queue<SocketChannel> socketChannelQueue;
    private ServerSocketChannel serverSocketChannel;

    public SocketAcceptor(Queue<SocketChannel> socketChannelQueue) {
        this.socketChannelQueue = socketChannelQueue;
    }

    @Override
    public void run() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(2010));
            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannelQueue.add(socketChannel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
