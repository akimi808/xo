package com.akimi808.xo.server;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * Created by akimi808 on 19/02/2018.
 */
public class SocketProcessor implements Runnable {
    private Queue<SocketChannel> socketChannelQueue;
    private Selector selector;

    public SocketProcessor(Queue<SocketChannel> socketChannelQueue) {
        this.socketChannelQueue = socketChannelQueue;
    }

    @Override
    public void run() {
        while (true ) {
            executeCycle();
        }
    }

    private void executeCycle() {
        takeNewSockets();
        readFromSockets();
        writeToSockets();
    }

    private void takeNewSockets() {
        while (true) {
            SocketChannel socketChannel = socketChannelQueue.poll();
            if (!socketChannel.equals(null)) {
                try {
                    SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
