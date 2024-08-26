package de.obsidiancloud.common.network.packets;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/** Updates the status of a server. */
public class ServerStatusChangePacket extends Packet {
    private String name;
    private OCServer.Status status;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, name);
        writeString(byteBuf, status.toString());
    }

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        name = readString(byteBuf);
        status = OCServer.Status.valueOf(readString(byteBuf));
    }

    /**
     * Gets the name of the server.
     *
     * @return The name of the server.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Sets the name of the server.
     *
     * @param name The name of the server.
     */
    public void setName(@NotNull String name) {
        this.name = name;
    }

    /**
     * Gets the status of the server.
     *
     * @return The status of the server.
     */
    public @NotNull OCServer.Status getStatus() {
        return status;
    }

    /**
     * Sets the status of the server.
     *
     * @param status The status of the server.
     */
    public void setStatus(@NotNull OCServer.Status status) {
        this.status = status;
    }
}
