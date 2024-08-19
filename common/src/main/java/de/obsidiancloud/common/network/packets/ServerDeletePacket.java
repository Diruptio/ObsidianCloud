package de.obsidiancloud.common.network.packets;

import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/** Deletes a server. */
public class ServerDeletePacket extends Packet {
    private String name;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, name);
    }

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, name);
    }

    /**
     * Gets the name of the server
     *
     * @return The name of the server
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Sets the name of the server
     *
     * @param name The name of the server
     */
    public void setName(@NotNull String name) {
        this.name = name;
    }
}
