package com.akimi808.xo.client;

import com.akimi808.xo.server.XoServer;
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
        new XoClient().run();
    }


    public XoClient() {
        clientProtocol = new ClientProtocol();
    }

    private void run() {
        Socket socket = null;
        try {
            socket = new Socket("localhost", 2810);
            log.debug("Connection established");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = null;
            while (!clientProtocol.getClientState().equals(ClientState.TERMINATE)) {
                String message = clientProtocol.processMessage(line);
                log.debug("Going to send [{}]", message);
                writer.write(message + "\n");
                writer.flush();
                line = reader.readLine();
                log.debug("Received [{}]", line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
