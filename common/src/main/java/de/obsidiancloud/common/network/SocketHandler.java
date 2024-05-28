package de.obsidiancloud.common.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.SocketChannel;
import org.jetbrains.annotations.NotNull;

public class SocketHandler {
    private final SocketChannel socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    public SocketHandler(@NotNull SocketChannel socket) {
        this.socket = socket;
        try {
            this.inputStream = new ObjectInputStream(socket.socket().getInputStream());
            this.outputStream = new ObjectOutputStream(socket.socket().getOutputStream());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Get the socket.
     *
     * @return The socket.
     */
    public @NotNull SocketChannel getSocket() {
        return socket;
    }

    /**
     * Get the input stream of the socket.
     *
     * @return The input stream of the socket.
     */
    public @NotNull ObjectInputStream getInputStream() {
        return inputStream;
    }

    /**
     * Get the output stream of the socket.
     *
     * @return The output stream of the socket.
     */
    public @NotNull ObjectOutputStream getOutputStream() {
        return outputStream;
    }
}
