package de.obsidiancloud.platform.network.packets;

import de.obsidiancloud.common.network.WritablePacket;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/** Sent from the server to the network when a player leaves the server */
public class S2NPlayerLeavePacket extends WritablePacket {
    private UUID uuid;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeUUID(byteBuf, uuid);
    }

    /**
     * Sets the UUID of the player
     *
     * @param uuid the UUID of the player
     */
    public void setUUID(@NotNull UUID uuid) {
        this.uuid = uuid;
    }
}
