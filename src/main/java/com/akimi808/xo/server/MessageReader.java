package com.akimi808.xo.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.akimi808.xo.common.Message;

/**
 * Created by akimi808 on 05/03/2018.
 */
public class MessageReader {

    private ArrayList<Message> listOfRequests = new ArrayList<>();
    private ByteBuffer byteBuffer = ByteBuffer.allocate(2048);


    public List<Message> decodeMessage(ByteBuffer readBytes) {

        copyFromBuffer(readBytes);
        if (Message.hasComplete(byteBuffer)) {
            listOfRequests.add(Message.read(byteBuffer));
            byteBuffer.compact();
        }
        readBytes.clear();
        return listOfRequests;
    }

    private void copyFromBuffer(ByteBuffer readBytes) {
        while (readBytes.hasRemaining() && byteBuffer.hasRemaining()) {
            byteBuffer.put(readBytes.get());
        }
        if (readBytes.hasRemaining()){
            throw new RuntimeException();
        }
    }
}
