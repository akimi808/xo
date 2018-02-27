package com.akimi808.xo.server;

import com.akimi808.xo.client.ClientProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger log = LogManager.getLogger(SocketAcceptor.class);

    public SocketAcceptor(Queue<SocketChannel> socketChannelQueue) {
        this.socketChannelQueue = socketChannelQueue;
    }

    @Override
    public void run() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(2010));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        while (true) {
            try {
                SocketChannel socketChannel = this.serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                log.debug("Socket accepted" + socketChannel); //?
                this.socketChannelQueue.add(socketChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


