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
    private Selector selector;
    private Queue<Tuple> outboundResponses = new ArrayDeque<>();


    public SocketProcessor(Queue<Client> clientQueue) throws IOException {
        this.clientQueue = clientQueue;
        this.selector = Selector.open();
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
                    SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
                    key.attach(client);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readFromSockets() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16 * 1024);
        if (selector.selectNow() > 0) {
            Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
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
                }
                selectionKeyIterator.remove();
            }
        }
    }


    private void writeToSockets() {
        //когда хотим записать в несколько сокетов, мы должна все сокеты, куда хотим записать, .
        // Помним, что записываем не всё, что у нас есть. Проверть, записали ли всё сообщение, запомнить это.
        //Как только записали сообщение целиком, долдна удалить сокет из селектора
    }


}




