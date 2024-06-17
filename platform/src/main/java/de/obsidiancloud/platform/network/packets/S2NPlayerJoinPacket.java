package de.obsidiancloud.platform.network.packets;

import de.obsidiancloud.common.network.WritablePacket;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/** Sent from the server to the network when a player joins the server */
public class S2NPlayerJoinPacket extends WritablePacket {
    private UUID uuid;
    private String name;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeUUID(byteBuf, uuid);
        writeString(byteBuf, name);
    }

    /**
     * Sets the UUID of the player
     *
     * @param uuid the UUID of the player
     */
    public void setUUID(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Sets the name of the player
     *
     * @param name the name of the player
     */
    public void setName(@NotNull String name) {
        this.name = name;
    }
}
