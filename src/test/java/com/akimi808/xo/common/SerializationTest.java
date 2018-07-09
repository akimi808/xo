package com.akimi808.xo.common;

import com.akimi808.xo.common.Message;
import com.akimi808.xo.common.Request;
import com.akimi808.xo.common.Type;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

/**
 * Created by akimi808 on 26/02/2018.
 */
public class SerializationTest {
    @Test
    public void testSerializeSimpleRequest() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Request request = new Request(1, "methodName", new Type[0], new Object[0]);
        request.write(buffer);
        byte[] expected = {0, 21,
                1,
                0, 0, 0, 1,
                0, 10, 109, 101, 116, 104, 111, 100, 78, 97, 109, 101,
                0, 0};
        buffer.flip();
        byte[] dst = new byte[21];
        buffer.get(dst);
        assertArrayEquals(expected, dst);
    }

    @Test
    public void testSerializeRequestWithArguments() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Type[] types = {Type.INTEGER, Type.STRING};
        Object[] values = {768345, "two words"};
        Request request = new Request(1, "methodWithArguments", types, values);
        request.write(buffer);
        byte[] expected = {0, 47,
                1,
                0, 0, 0, 1,
                0, 19,
                109, 101, 116, 104, 111, 100, 87, 105, 116, 104, 65, 114, 103, 117, 109, 101, 110, 116, 115,
                0, 2,
                1, 2,
                0, 11, -71, 89,
                0, 9,
                116, 119, 111, 32, 119, 111, 114, 100, 115
        };
        buffer.flip();
        byte[] dst = new byte[47];
        buffer.get(dst);
        assertArrayEquals(expected, dst);
    }

    @Test
    public void testDeserializeSimpleRequest() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byte[] expected = {0, 21,
                1,
                0, 0, 0, 1,
                0, 10, 109, 101, 116, 104, 111, 100, 78, 97, 109, 101,
                0, 0};
        for (int i = 0; i < expected.length; i++) {
            byteBuffer.put(expected[i]);
        }
        byteBuffer.flip();
        Message message = Message.read(byteBuffer);
        Request request = new Request(1, "methodName", new Type[0], new Object[0]);
        assertEquals(message, request);

    }
}