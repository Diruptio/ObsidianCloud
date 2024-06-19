package de.obsidiancloud.node.network.packets;

import de.obsidiancloud.common.network.ReadablePacket;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/** Sent from the server to the network when a player leaves the server */
public class S2NPlayerLeavePacket extends ReadablePacket {
    private UUID uuid;

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        uuid = readUUID(byteBuf);
    }

    /**
     * Gets the UUID of the player
     *
     * @return the UUID of the player
     */
    public UUID getUUID() {
        return uuid;
    }
}
