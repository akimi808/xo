package com.akimi808.xo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Created by akimi808 on 19/02/2018.
 */
public class SocketProcessor implements Runnable {
    private Queue<Client> clientQueue;
    private Selector readSelector;
    private Selector writeSelector;
    private static final Logger log = LogManager.getLogger(SocketProcessor.class);
    private Set<SelectionKey> keysToCancel = new HashSet<>();
    private Dispatcher dispatcher;


    public SocketProcessor(Queue<Client> clientQueue, Dispatcher dispatcher) throws IOException {
        this.clientQueue = clientQueue;
        this.dispatcher = dispatcher;
        this.readSelector = Selector.open();
        this.writeSelector = Selector.open();
    }

    @Override
    public void run() {
        while (true) {
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
        while (!clientQueue.isEmpty()) {
            Client client = clientQueue.poll();
            if (client != null) {
                try {
                    SocketChannel socketChannel = client.getSocketChannel();
                    SelectionKey key = socketChannel.register(readSelector, SelectionKey.OP_READ);
                    key.attach(client);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readFromSockets() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16 * 1024);
        SelectionKey keyWrite;
        if (readSelector.selectNow() > 0) {
            Iterator<SelectionKey> selectionKeyIterator = readSelector.selectedKeys().iterator();
            while (selectionKeyIterator.hasNext()) {
                SelectionKey selectionKey = selectionKeyIterator.next();
                Client client = (Client) selectionKey.attachment();
                SocketChannel socketChannel = client.getSocketChannel();
                MessageReader messageReader = client.getMessageReader();
                ServerProtocol serverProtocol = client.getServerProtocol();
                if (socketChannel.read(byteBuffer) == -1) {
                    socketChannel.close();
                    selectionKey.attach(null);
                    selectionKey.cancel();
                } else {
                    byteBuffer.flip();
                    log.debug("Read bytes [{}]", byteBuffer);
                    List<Message> messages = messageReader.decodeMessage(byteBuffer);
                    for (Iterator<Message> iterator = messages.iterator(); iterator.hasNext(); ) {
                        Message message = iterator.next();
                        log.debug("Received message: [{}]", message.getText());
                        String response = dispatcher.dispatch(message, client);//serverProtocol.processMessage(message.getText());
                        log.debug("Response for client [{}]", response);
                        client.getMessageWriter().enqueue(response);
                        keyWrite = socketChannel.register(writeSelector, SelectionKey.OP_WRITE);
                        keyWrite.attach(client);
                        keysToCancel.remove(keyWrite);
                        iterator.remove();
                    }
                }
                selectionKeyIterator.remove();
                byteBuffer.clear();
            }
        }
    }

    private void writeToSockets() throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.allocate(16 * 1024);
        delayedCancel();
        if (writeSelector.selectNow() > 0) {
            Iterator<SelectionKey> selectionKeyIterator = writeSelector.selectedKeys().iterator();
            while (selectionKeyIterator.hasNext()) {
                SelectionKey selectionKey = selectionKeyIterator.next();
                Client client = (Client) selectionKey.attachment();
                MessageWriter mw = client.getMessageWriter(); //
                mw.writeToSocket(client.getSocketChannel(), writeBuffer);
                if (mw.isComplete()) {
                    keysToCancel.add(selectionKey);
                }
                writeBuffer.clear();
                selectionKeyIterator.remove();
            }
        }
    }

    private void delayedCancel() {
        for (Iterator<SelectionKey> iterator = keysToCancel.iterator(); iterator.hasNext(); ) {
            SelectionKey next = iterator.next();
            next.cancel();
            iterator.remove();
        }
    }
}






