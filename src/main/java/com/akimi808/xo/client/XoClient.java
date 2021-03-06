package com.akimi808.xo.client;

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
public class XoClient {
    private static final Logger log = LogManager.getLogger(XoClient.class);
    private final ClientProtocol clientProtocol;

    public static void main(String[] args) {
        new XoClient(args[0]).run();
    }


    public XoClient(String name) {
        clientProtocol = new ClientProtocol(name);
    }

    private void run() {
        try (Socket socket = new Socket("localhost", 2810)) {
            log.debug("Connection established");
            try {
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = null;
                while (!clientProtocol.getClientState().equals(ClientState.TERMINATE)) {
                    String message = clientProtocol.processMessage(line);
                    if (message != null) {
                        log.debug("Going to send [{}]", message);
                        writer.write(message + "\n");
                        writer.flush();
                        line = reader.readLine();
                        log.debug("Received [{}]", line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
