package com.akimi808.xo.server;

import com.akimi808.xo.common.Message;
import com.akimi808.xo.common.RingBuffer;

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
    private Queue<Message> outboundResponses = new ArrayDeque<>();
    private RingBuffer buffer = new RingBuffer(2048);

    public void writeToSocket(SocketChannel socketChannel, ByteBuffer writeBuffer) throws IOException {
        while (true) {
            if (buffer.available() == 0 && outboundResponses.isEmpty()) {
                break;
            }
            if (buffer.available() > 0) {
                buffer.readToByteBuffer(writeBuffer);
            }

            final Message message = outboundResponses.poll();
            if (message != null) {
                message.write(writeBuffer);
            }

            socketChannel.write(writeBuffer);
            if (writeBuffer.remaining() > 0) {
                buffer.writeFromByteBuffer(writeBuffer);
                break;
            }
        }
    }

    public void enqueue(Message message) {
        outboundResponses.offer(message);
    }

    public boolean isComplete() {
        return buffer.available() == 0 && outboundResponses.isEmpty();
    }

}
