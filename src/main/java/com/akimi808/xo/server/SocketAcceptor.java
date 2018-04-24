package com.akimi808.xo.server;

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
    private Queue<Client> clientQueue;
    private ServerSocketChannel serverSocketChannel;
    private static final Logger log = LogManager.getLogger(SocketAcceptor.class);
    private XoServer xoServer;

    public SocketAcceptor(Queue<Client> clientQueue, XoServer xoServer) {
        this.clientQueue = clientQueue;
        this.xoServer = xoServer;
    }

    @Override
    public void run() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(2810));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        while (true) {
            try {
                SocketChannel socketChannel = this.serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
//                socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 1);
                log.debug("Socket accepted" + socketChannel);
                this.clientQueue.add(new Client(socketChannel, xoServer));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


