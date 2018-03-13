package com.akimi808.xo.server;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akimi808 on 05/03/2018.
 */
public class MessageReader {

    private ArrayList<Message> listOfMessages = new ArrayList<>();
    private byte[] incomplete = new byte[1024];
    int offset = 0;

    public List<Message> decodeMessage(ByteBuffer readBytes) {
        String completeMessageStr = "";
        try {
            while (readBytes.hasRemaining()) {
                byte b = readBytes.get();
                if (b != '\n') {
                    incomplete[offset] = b;
                    offset++;
                } else {
                    completeMessageStr = new String(incomplete, 0, offset, "ISO-8859-1");
                    listOfMessages.add(new Message(completeMessageStr));
                    offset = 0;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return listOfMessages;
    }

    public String getIncompleteMessage() {
        String incompleteMessage = "";
        if (offset != 0) {
            try {
                incompleteMessage = new String(incomplete, 0, offset, "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return incompleteMessage;
    }
}
