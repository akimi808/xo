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
    private Queue<Tuple> outboundResponses = new ArrayDeque<>();
    private static final Logger log = LogManager.getLogger(SocketProcessor.class);
    private Set<SelectionKey> keysToCancel = new HashSet<>();


    public SocketProcessor(Queue<Client> clientQueue) throws IOException {
        this.clientQueue = clientQueue;
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
                socketChannel.read(byteBuffer);
                byteBuffer.flip();
                log.debug("Read bytes [{}]", byteBuffer);
                List<Message> messages = messageReader.decodeMessage(byteBuffer);
                for (Iterator<Message> iterator = messages.iterator(); iterator.hasNext(); ) {
                    Message message = iterator.next();
                    log.debug("Received message: [{}]", message.getText());
                    String response = serverProtocol.processMessage(message.getText());
                    log.debug("Response for client [{}]", response);
                    outboundResponses.add(new Tuple(response, client));
                    keyWrite = socketChannel.register(writeSelector, SelectionKey.OP_WRITE);
                    keyWrite.attach(client);
                    keysToCancel.remove(keyWrite);
                    iterator.remove();
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
                for (Iterator<Tuple> iterator = outboundResponses.iterator(); iterator.hasNext(); ) {
                    Tuple tuple = iterator.next();
                    if (tuple.getClient().equals(client)) {
                        SocketChannel socketChannel = client.getSocketChannel();
                        String messageText = tuple.getResponse();
                        byte[] messageTextBytes = messageText.getBytes();
                        writeBuffer.put(messageTextBytes);
                        writeBuffer.put((byte)'\n');
                        writeBuffer.flip();
                        int written = socketChannel.write(writeBuffer);
                        if (written == messageTextBytes.length + 1) {
                            keysToCancel.add(selectionKey);
                            iterator.remove();
                        } else {
                            String newResponse = new String(messageTextBytes, written, messageTextBytes.length - written, "ISO-8859-1");
                            tuple.setResponse(newResponse);
                        }
                        writeBuffer.clear();
                    }
                }
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






