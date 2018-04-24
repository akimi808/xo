package com.akimi808.xo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by akimi808 on 12/11/2017.
 */
public class XoServer {
    private static final Logger log = LogManager.getLogger(XoServer.class);

    public static void main(String[] args) {
        try {
            new XoServer().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() throws IOException {
        Queue<Client> clientQueue = new ArrayBlockingQueue<>(1000);
        GameManager gameManager = new GameManager();
        new Thread(new SocketAcceptor(clientQueue, this, gameManager)).start();
        new Thread(new SocketProcessor(clientQueue)).start();
        log.debug("Threads created");
    }
}
