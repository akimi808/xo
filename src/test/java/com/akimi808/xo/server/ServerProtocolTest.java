package com.akimi808.xo.server;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Created by akimi808 on 26/02/2018.
 */
public class ServerProtocolTest {
    @Test
    public void testDecodeOneMessage() throws Exception {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.put(new byte[] {'a', 'b', 'c', '\n'});
        bb.flip();
        MessageReader messageReader = new MessageReader();
        assertEquals(Arrays.asList(new Message(true, "abc")), messageReader.decodeMessage(bb));
    }

    @Test
    public void testDecodeBigMessage() throws Exception {
        ByteBuffer bb = ByteBuffer.allocate(30);
        bb.put(new byte[] {'a', 'b', 'c', '\n'});
        bb.flip();
        MessageReader messageReader = new MessageReader();
        assertEquals(Arrays.asList(new Message(true, "abc")), messageReader.decodeMessage(bb));
    }

    @Test
    public void testDecodeTwoMessages() throws Exception {
        ByteBuffer bb = ByteBuffer.allocate(30);
        bb.put(new byte[] {'a', 'b', 'c', '\n', 'c', 'd', 'e', '\n'});
        bb.flip();
        MessageReader messageReader = new MessageReader();
        assertEquals(Arrays.asList(new Message(true, "abc"),
                new Message(true,"cde")), messageReader.decodeMessage(bb));
    }

    @Test
    public void testDecodeOneAndIncompleteMessages() throws Exception {
        ByteBuffer bb = ByteBuffer.allocate(30);
        bb.put(new byte[] {'a', 'b', 'c', '\n', 'c', 'd', 'e'});
        bb.flip();
        MessageReader messageReader = new MessageReader();
        assertEquals(Arrays.asList(new Message(true, "abc"),
                new Message(false, "cde")), messageReader.decodeMessage(bb));
    }

    @Test
    public void testSplitedMessage() throws Exception {
        MessageReader messageReader = new MessageReader();
        ByteBuffer bb = ByteBuffer.allocate(30);
        bb.put(new byte[] {'a', 'b', 'c'});
        bb.flip();
        ArrayList<Message> actual = messageReader.decodeMessage(bb);
        assertEquals(Arrays.asList(new Message(false, "abc")), actual);
        // Here was delay and next round of reads
        bb.clear();
        bb.put(new byte[] {'c', 'd', 'e', '\n'});
        bb.flip();
        assertEquals(Arrays.asList(new Message(true, "abccde")), messageReader.decodeMessage(bb));
    }

    @Test
    public void testMultipleConnections() throws Exception {
        MessageReader mr1 = new MessageReader();
        ByteBuffer bb = ByteBuffer.allocate(30);
        // Reading from first socket
        bb.put(new byte[] {'a', 'b', 'c'});
        bb.flip();
        ArrayList<Message> actual = mr1.decodeMessage(bb);
        assertEquals(Arrays.asList(new Message(false, "abc")), actual);
        bb.clear();
        // Reading from second socket
        MessageReader mr2 = new MessageReader();
        bb.put(new byte[] {'c', 'd', 'e', '\n'});
        bb.flip();
        assertEquals(Arrays.asList(new Message(true, "cde")), mr2.decodeMessage(bb));
        bb.clear();
        // Reading from first socket
        bb.put(new byte[] {'\n'});
        bb.flip();
        assertEquals(Arrays.asList(new Message(true, "abc")), mr1.decodeMessage(bb));

    }
}