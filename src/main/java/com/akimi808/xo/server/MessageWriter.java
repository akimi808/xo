package com.akimi808.xo.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by akimi808 on 25/03/2018.
 */
public class MessageWriter {
    private Queue<String> outboundResponses = new ArrayDeque<>();
    private int currentWritten = 0;
    private int currentSize = 0;
    private byte[] messageTextBytes;

    public void writeToSocket(SocketChannel socketChannel, ByteBuffer writeBuffer) throws IOException {
        if (currentSize <= currentWritten) {
            String messageText = outboundResponses.poll();
            messageTextBytes = messageText != null ? (messageText + "\n").getBytes() : null;
            currentSize = messageTextBytes != null ? messageTextBytes.length : 0;
            currentWritten = 0;
        }
        if (messageTextBytes != null) {
            writeBuffer.put(messageTextBytes, currentWritten, messageTextBytes.length - currentWritten);
            writeBuffer.flip();
            currentWritten += socketChannel.write(writeBuffer);
        }
    }

    public void enqueue(String response) {
        outboundResponses.offer(response);
    }

    public boolean isComplete() {
        return currentWritten == currentSize && outboundResponses.isEmpty();
    }

}
