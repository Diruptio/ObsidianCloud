package de.obsidiancloud.common.network.packets;

import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Sends a message to a player. */
public class PlayerKickPacket extends Packet {
    private UUID uuid;
    private Component message;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeUUID(byteBuf, uuid);
        byteBuf.writeBoolean(message != null);
        if (message != null) {
            writeString(byteBuf, GsonComponentSerializer.gson().serialize(message));
        }
    }

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        this.uuid = readUUID(byteBuf);
        if (byteBuf.readBoolean()) {
            this.message = GsonComponentSerializer.gson().deserialize(readString(byteBuf));
        } else {
            this.message = null;
        }
    }

    /**
     * Gets The UUID of the player
     *
     * @return The UUID of the player
     */
    public @NotNull UUID getUUID() {
        return uuid;
    }

    /**
     * Sets The UUID of the player
     *
     * @param uuid The UUID of the player
     */
    public void setUUID(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the message for the player
     *
     * @return The message for the player
     */
    public @Nullable Component getMessage() {
        return message;
    }

    /**
     * Sets the message for the player
     *
     * @param message The message for the player
     */
    public void setMessage(@Nullable Component message) {
        this.message = message;
    }
}
