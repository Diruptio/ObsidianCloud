package de.obsidiancloud.common.network.packets;

import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/** Sent when a server was deleted. */
public class ServerRemovedPacket extends Packet {
    private String serverName;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, serverName);
    }

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, serverName);
    }

    /**
     * Gets the name of the server
     *
     * @return The name of the server
     */
    public @NotNull String getServerName() {
        return serverName;
    }

    /**
     * Sets the name of the server
     *
     * @param serverName The name of the server
     */
    public void setServerName(@NotNull String serverName) {
        this.serverName = serverName;
    }
}
