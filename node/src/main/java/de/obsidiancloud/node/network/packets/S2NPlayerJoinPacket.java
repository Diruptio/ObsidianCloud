package de.obsidiancloud.node.network.packets;

import de.obsidiancloud.common.network.ReadablePacket;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/** Sent from the server to the network when a player joins the server */
public class S2NPlayerJoinPacket extends ReadablePacket {
    private UUID uuid;
    private String name;

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        uuid = readUUID(byteBuf);
        name = readString(byteBuf);
    }

    /**
     * Gets the UUID of the player
     *
     * @return the UUID of the player
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Gets the name of the player
     *
     * @return the name of the player
     */
    public String getName() {
        return name;
    }
}
