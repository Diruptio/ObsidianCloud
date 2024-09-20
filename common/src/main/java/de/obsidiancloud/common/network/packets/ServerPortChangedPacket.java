package de.obsidiancloud.common.network.packets;

import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/** Sent when the port of a server was updated. */
public class ServerPortChangedPacket extends Packet {
    private String name;
    private int port;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, name);
        byteBuf.writeInt(port);
    }

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        name = readString(byteBuf);
        port = byteBuf.readInt();
    }

    /**
     * Gets the name of the server.
     *
     * @return The name
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Sets the name of the server.
     *
     * @param name The name
     */
    public void setName(@NotNull String name) {
        this.name = name;
    }

    /**
     * Gets the port of the server.
     *
     * @return The port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port of the server.
     *
     * @param port The port
     */
    public void setPort(int port) {
        this.port = port;
    }
}
