package com.akimi808.xo.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;

import com.akimi808.xo.common.Response;
import com.akimi808.xo.common.Update;

/**
 * @author Andrey Larionov
 */
public class XoClient2 {
    private SocketProcessor processor;
    public void connect() {
        try {
            SocketChannel socket = SocketChannel.open(new InetSocketAddress("localhost", 2810));
            socket.configureBlocking(false);
            processor = new SocketProcessor(socket);
            processor.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void login(String name) {
        
    }

    public List<Update> getUpdates() {
        return null;
    }

    public Response makeMove(Integer gameId, Integer selectedMove) {
        return null;
    }

    public void disconnect() {

    }

    private static class SocketProcessor extends Thread {
        private SocketChannel socket;
        private Selector readSelector;
        private Selector writeSelector;
        private boolean terminate = false;

        public SocketProcessor(SocketChannel socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                readSelector = Selector.open();
                writeSelector = Selector.open();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                socket.register(readSelector, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }

            while (!terminate) {
                readFromSocket();
                writeToSocket();
            }

            try {
                readSelector.close();
            } catch (IOException e) {
            }
            try {
                writeSelector.close();
            } catch (IOException e) {
            }
            try {
                socket.close();
            } catch (IOException e) {
            }
        }

        private void readFromSocket() throws IOException {
            if (readSelector.selectNow() > 0) {

            }

        }

        private void writeToSocket() {

        }
    }
}
