package com.akimi808.xo.server;

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


    public SocketProcessor(Queue<Client> clientQueue) throws IOException {
        this.clientQueue = clientQueue;
        this.readSelector = Selector.open();
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
                List<Message> messages = messageReader.decodeMessage(byteBuffer);
                for (Message message : messages) {
                    String response = serverProtocol.processMessage(message.getText());
                    outboundResponses.add(new Tuple(response, client));
                    keyWrite = socketChannel.register(writeSelector, SelectionKey.OP_WRITE);
                    keyWrite.attach(client);
                }
                selectionKeyIterator.remove();
            }
        }
    }


    private void writeToSockets() throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.allocate(16 * 1024);
        if (writeSelector.selectNow() > 0) {
            Iterator<SelectionKey> selectionKeyIterator = writeSelector.selectedKeys().iterator();
            while (selectionKeyIterator.hasNext()) {
                SelectionKey selectionKey = selectionKeyIterator.next();
                Tuple tuple = (Tuple) selectionKey.attachment();
                while (!outboundResponses.isEmpty()) {
                    tuple = outboundResponses.element();
                    SocketChannel socketChannel = tuple.getClient().getSocketChannel();
                    String messageText = tuple.getResponse();
                    byte[] messageTextBytes = messageText.getBytes();
                    writeBuffer.put(messageTextBytes);
                    int writed = socketChannel.write(writeBuffer);
                    if (writed == messageTextBytes.length) {
                        socketChannel.close();
                        outboundResponses.remove(tuple);
                        selectionKeyIterator.remove();
                    } else {
                        String newResponse = new String(messageTextBytes, writed, messageTextBytes.length, "ISO-8859-1");
                        tuple.setResponse(newResponse);
                    }
                        //изменить в очереди из таплов, в тапле response, сообщение (вычесть
                    }

    }

}




