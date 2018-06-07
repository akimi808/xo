package com.akimi808.xo.server;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by akimi808 on 26/02/2018.
 */
public class ServerProtocolTest {
/*
    @Test
    public void testDecodeOneMessage() throws Exception {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.put(new byte[] {'a', 'b', 'c', '\n'});
        bb.flip();
        MessageReader messageReader = new MessageReader();
        assertEquals(Arrays.asList(new Message("abc")), messageReader.decodeMessage(bb));
    }

    @Test
    public void testDecodeBigMessage() throws Exception {
        ByteBuffer bb = ByteBuffer.allocate(30);
        bb.put(new byte[] {'a', 'b', 'c', '\n'});
        bb.flip();
        MessageReader messageReader = new MessageReader();
        assertEquals(Arrays.asList(new Message("abc")), messageReader.decodeMessage(bb));
    }

    @Test
    public void testDecodeTwoMessages() throws Exception {
        ByteBuffer bb = ByteBuffer.allocate(30);
        bb.put(new byte[] {'a', 'b', 'c', '\n', 'c', 'd', 'e', '\n'});
        bb.flip();
        MessageReader messageReader = new MessageReader();
        assertEquals(Arrays.asList(new Message("abc"),
                new Message("cde")), messageReader.decodeMessage(bb));
    }

    @Test
    public void testDecodeOneAndIncompleteMessages() throws Exception {
        ByteBuffer bb = ByteBuffer.allocate(30);
        bb.put(new byte[] {'a', 'b', 'c', '\n', 'c', 'd', 'e'});
        bb.flip();
        MessageReader messageReader = new MessageReader();
        assertEquals(
                Arrays.asList(new Message("abc")),
                messageReader.decodeMessage(bb));
        assertEquals("cde",  messageReader.getIncompleteMessage());
    }

    @Test
    public void testSplitedMessage() throws Exception {
        MessageReader messageReader = new MessageReader();
        ByteBuffer bb = ByteBuffer.allocate(30);
        bb.put(new byte[] {'a', 'b', 'c'});
        bb.flip();
        List<Message> actual = messageReader.decodeMessage(bb);
        assertEquals(Collections.emptyList(), actual);
        assertEquals("abc",  messageReader.getIncompleteMessage());
        // Here was delay and next round of reads
        bb.clear();
        bb.put(new byte[] {'c', 'd', 'e', '\n'});
        bb.flip();
        assertEquals(Arrays.asList(new Message("abccde")), messageReader.decodeMessage(bb));
        assertEquals("",  messageReader.getIncompleteMessage());
    }

    @Test
    public void testMultipleConnections() throws Exception {
        MessageReader mr1 = new MessageReader();
        ByteBuffer bb = ByteBuffer.allocate(30);
        // Reading from first socket
        bb.put(new byte[] {'a', 'b', 'c'});
        bb.flip();
        List<Message> actual = mr1.decodeMessage(bb);
        assertEquals(Collections.emptyList(), actual);
        bb.clear();
        // Reading from second socket
        MessageReader mr2 = new MessageReader();
        bb.put(new byte[] {'c', 'd', 'e', '\n'});
        bb.flip();
        assertEquals(Arrays.asList(new Message("cde")), mr2.decodeMessage(bb));
        bb.clear();
        // Reading from first socket
        bb.put(new byte[] {'\n'});
        bb.flip();
        assertEquals(Arrays.asList(new Message("abc")), mr1.decodeMessage(bb));

    }
*/
}