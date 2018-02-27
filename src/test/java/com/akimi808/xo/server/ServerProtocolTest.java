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
        ServerProtocol serverProtocol = new ServerProtocol(new XoServer());
        assertEquals(Arrays.asList(new Message(true, "abc")), serverProtocol.decodeMessage(bb));
    }

    @Test
    public void testDecodeBigMessage() throws Exception {
        ByteBuffer bb = ByteBuffer.allocate(30);
        bb.put(new byte[] {'a', 'b', 'c', '\n'});
        bb.flip();
        ServerProtocol serverProtocol = new ServerProtocol(new XoServer());
        assertEquals(Arrays.asList(new Message(true, "abc")), serverProtocol.decodeMessage(bb));
    }

    @Test
    public void testDecodeTwoMessages() throws Exception {
        ByteBuffer bb = ByteBuffer.allocate(30);
        bb.put(new byte[] {'a', 'b', 'c', '\n', 'c', 'd', 'e', '\n'});
        bb.flip();
        ServerProtocol serverProtocol = new ServerProtocol(new XoServer());
        assertEquals(Arrays.asList(new Message(true, "abc"),
                new Message(true,"cde")), serverProtocol.decodeMessage(bb));
    }

    @Test
    public void testDecodeOneAndIncompleteMessages() throws Exception {
        ByteBuffer bb = ByteBuffer.allocate(30);
        bb.put(new byte[] {'a', 'b', 'c', '\n', 'c', 'd', 'e'});
        bb.flip();
        ServerProtocol serverProtocol = new ServerProtocol(new XoServer());
        assertEquals(Arrays.asList(new Message(true, "abc"),
                new Message(false, "cde")), serverProtocol.decodeMessage(bb));
    }

    @Test
    public void testSplitedMessage() throws Exception {
        ServerProtocol serverProtocol = new ServerProtocol(new XoServer());
        ByteBuffer bb = ByteBuffer.allocate(30);
        bb.put(new byte[] {'a', 'b', 'c'});
        bb.flip();
        ArrayList<Message> actual = serverProtocol.decodeMessage(bb);
        assertEquals(Arrays.asList(new Message(false, "abc")), actual);
        // Here was delay and next round of reads
        bb.clear();
        bb.put(new byte[] {'c', 'd', 'e', '\n'});
        bb.flip();
        assertEquals(Arrays.asList(new Message(true, "abccde")), serverProtocol.decodeMessage(bb));
    }
}