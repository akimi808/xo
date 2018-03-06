package com.akimi808.xo.server;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akimi808 on 05/03/2018.
 */
public class MessageReader {

    private ArrayList<Message> listOfMessages = new ArrayList<>();


    public List<Message> decodeMessage(ByteBuffer readBytes) {
        byte[] incompleteMessage = new byte[readBytes.remaining()];
        String completeMessageStr = "";
        String incompleteMessageStr = "";
        int i = 0;
        try {
            while (readBytes.hasRemaining()) {
                byte b = readBytes.get();
                if (b != '\n') {
                    incompleteMessage[i] = b;
                    i++;
                } else {
                    if (listOfMessages.size() == 0 || listOfMessages.get(listOfMessages.size() - 1).isComplete()) {
                        completeMessageStr = new String(incompleteMessage, 0, i, "ISO-8859-1");
                        listOfMessages.add(new Message(true, completeMessageStr));
                    } else {
                        Message partOfMessage = listOfMessages.get(listOfMessages.size() - 1);
                        String secondPart = new String(incompleteMessage, 0, i, "ISO-8859-1");
                        partOfMessage.setText(partOfMessage.getText() + secondPart);
                        partOfMessage.setComplete(true);
                    }
                    incompleteMessage = new byte[readBytes.remaining()];
                    i = 0;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (i != 0) {
            try {
                incompleteMessageStr = new String(incompleteMessage, 0, i, "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            listOfMessages.add(new Message(false, incompleteMessageStr));
        }

        return listOfMessages;
    }

}
