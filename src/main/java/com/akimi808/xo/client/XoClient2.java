package com.akimi808.xo.client;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.akimi808.xo.common.*;
import com.akimi808.xo.server.Client;
import com.akimi808.xo.server.MessageReader;

/**
 * @author Andrey Larionov
 */
public class XoClient2 implements Handler {
    private final UpdateListener listener;
    private SocketProcessor processor;
    private Queue<Message> outboundQueue;
    private AtomicInteger requestSeq = new AtomicInteger(0);
    private Map<Integer, CompletableFuture<Response>> awaitingResponses = new HashMap<>();
    private Integer sessionId = null;

    public XoClient2(UpdateListener listener) {
        this.listener = listener;

    }

    public void connect() {
        try {
            SocketChannel socket = SocketChannel.open(new InetSocketAddress("localhost", 2810));
            socket.configureBlocking(false);
            outboundQueue = new ArrayBlockingQueue<>(100);
            processor = new SocketProcessor(socket, outboundQueue);
            processor.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlayerInfo login(String name) {
        Type[] types = {Type.STRING};
        Object[] args = {name};
        Request request = new Request(sessionId, requestSeq.incrementAndGet(), "login", types, args);
        try {
            return sendSync(request).thenApply(r -> (PlayerInfo) r.getResponseObject()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean makeMove(Integer gameId, Integer selectedMove) {
        Type[] types = {Type.INTEGER, Type.INTEGER};
        Object[] args = {gameId, selectedMove};
        Request request = new Request(sessionId, requestSeq.incrementAndGet(),"makeMove", types, args);
        try {
            return sendSync(request).thenApply(r -> (Boolean) r.getResponseObject()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<Response> sendSync(Request request) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        awaitingResponses.put(request.getRequestId(), future);
        outboundQueue.add(request);
        return future;
    }

    public void disconnect() {

    }

    @Override
    public void handleRequest(Request request) {
        // Actually server should not send requests

    }

    @Override
    public void handleResponse(Response response) {
        Integer requestId = response.getRequestId();
        awaitingResponses.get(requestId).complete(response);
    }

    @Override
    public void handleUpdate(Update update) {
        if (update instanceof UpdateSessionInitiated) {
            this.sessionId = update.getSessionId();
        }
        listener.onUpdate(update);

    }

    public boolean isSessionInitiated() {
        return sessionId != null;
    }

    private class SocketProcessor extends Thread {
        private SocketChannel socket;
        private Selector readSelector;
        private Selector writeSelector;
        private boolean terminate = false;
        private final ByteBuffer readBuffer = ByteBuffer.allocate(2048);
        private final ByteBuffer writeBuffer = ByteBuffer.allocate(2048);
        private final MessageReader messageReader = new MessageReader();
        private final Queue<Message> outboundQueue;
        private boolean writeComplete;

        public SocketProcessor(SocketChannel socket, Queue<Message> outboundQueue) {
            this.socket = socket;
            this.outboundQueue = outboundQueue;
        }

        @Override
        public void run() {
            try {
                readSelector = Selector.open();
                writeSelector = Selector.open();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                socket.register(readSelector, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }

            while (!terminate) {
                try {
                    readFromSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    writeToSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                readSelector.close();
            } catch (IOException e) {
            }
            try {
                writeSelector.close();
            } catch (IOException e) {
            }
            try {
                socket.close();
            } catch (IOException e) {
            }
        }

        private void readFromSocket() throws IOException {
            if (readSelector.selectNow() > 0) {
                for (Iterator<SelectionKey> iterator = readSelector.selectedKeys().iterator(); iterator.hasNext(); ) {
                    SelectionKey selectionKey = iterator.next();
                    if (socket.read(readBuffer) == -1) {
                        selectionKey.cancel();
                        terminate = true;
                    } else {
                        List<Message> messages = messageReader.decodeMessage(readBuffer);
                        for (Message message : messages) {
                            message.handle(XoClient2.this);
                        }
                    }
                    iterator.remove();
                    readBuffer.clear();
                }
            }
        }

        private void writeToSocket() throws IOException {
            boolean shouldSend = !outboundQueue.isEmpty() || !writeComplete;
            if (!shouldSend) {
                SelectionKey selectionKey = socket.keyFor(writeSelector);
                if (selectionKey != null) {
                    selectionKey.cancel();
                }
            } else {
                writeComplete = false;
                socket.register(writeSelector, SelectionKey.OP_WRITE);
            }
            if (writeSelector.selectNow() > 0) {
                boolean canWriteMore = true;
                if (writeBuffer.hasRemaining()) {
                    socket.write(writeBuffer);
                    canWriteMore = !writeBuffer.hasRemaining();
                }
                while (canWriteMore && outboundQueue.size() > 0) {
                    Message message = outboundQueue.poll();
                    message.write(writeBuffer);
                    writeBuffer.flip();
                    socket.write(writeBuffer);
                    canWriteMore = !writeBuffer.hasRemaining();
                    if (canWriteMore) {
                        writeBuffer.clear();
                    }
                }
                writeComplete = canWriteMore;
            }
        }

    }

}
