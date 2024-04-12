package de.obsidiancloud.node.networking;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

public class Server implements Closeable {
    private final ServerSocketChannel serverSocket;

    public Server(String bindAddress, int port) throws IOException {
        serverSocket = ServerSocketChannel.open();
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }
}
