package com.akimi808.xo.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by akimi808 on 19/02/2018.
 */
public class SocketProcessor implements Runnable {
    private Queue<SocketChannel> socketChannelQueue;
    private Selector selector;



    public SocketProcessor(Queue<SocketChannel> socketChannelQueue) throws IOException {
        this.socketChannelQueue = socketChannelQueue;
        this.selector = Selector.open();
    }

    @Override
    public void run() {
        while (true ) {
            try {
                executeCycle();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void executeCycle() throws IOException {
        takeNewSockets();
        readFromSockets();
        writeToSockets();
    }


    private void takeNewSockets() {
        while (true) {
            SocketChannel socketChannel = socketChannelQueue.poll();
            if (socketChannel == null) {break;}
            if (!socketChannel.equals(null)) {
                try {
                    SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
                    key.attach(socketChannel);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void readFromSockets() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16 * 1024);
        if (selector.selectNow() > 0) {
            for (SelectionKey selectionKey : selector.selectedKeys()) {
                SocketChannel socketChannel = (SocketChannel) selectionKey.attachment();
                socketChannel.read(byteBuffer);
                byteBuffer.flip();
                ServerProtocol serverProtocol = new ServerProtocol(new XoServer());
                ArrayList<Message> messages = serverProtocol.decodeMessage(byteBuffer);
            }
        }
    }


    private void writeToSockets() {
    }


}




