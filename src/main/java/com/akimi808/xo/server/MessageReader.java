package com.akimi808.xo.server;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.akimi808.xo.common.Message;
import com.akimi808.xo.common.Request;
import com.akimi808.xo.common.RingBuffer;

/**
 * Created by akimi808 on 05/03/2018.
 */
public class MessageReader {

    private ArrayList<Message> listOfRequests = new ArrayList<>();
    private ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
    private RingBuffer buffer = new RingBuffer(2048);

    public List<Message> decodeMessage(ByteBuffer readBytes) {
        while (readBytes.remaining() > 0) {
            if (buffer.writeFromByteBuffer(readBytes) <= 0) {
                // Out of capacity
                throw new RuntimeException();
            };
            if (Message.hasComplete(byteBuffer)) {
                listOfRequests.add(Message.read(byteBuffer));
            }
        }
        readBytes.clear();
        return listOfRequests;
    }
}
