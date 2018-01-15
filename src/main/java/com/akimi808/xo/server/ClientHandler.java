package com.akimi808.xo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by akimi808 on 12/11/2017.
 */
public class ClientHandler extends Thread {
    private static final Logger log = LogManager.getLogger(ClientHandler.class);
    private final ServerProtocol serverProtocol;
    private Socket socket;


    public ClientHandler(Socket socket, XoServer xoServer) {
        this.socket = socket;
        serverProtocol = new ServerProtocol(xoServer);
    }

    @Override
    public void run() {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!serverProtocol.getServerState().equals(ServerState.TERMINATE)) {
                String line = reader.readLine();
                log.debug("Received message from client [{}]", line);
                String message = serverProtocol.processMessage(line);
                if (message != null) {
                    log.debug("Going to send message [{}]", message);
                    writer.write(message + "\n");
                    writer.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
