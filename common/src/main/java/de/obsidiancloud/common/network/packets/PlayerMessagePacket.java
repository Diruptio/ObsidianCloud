package de.obsidiancloud.common.network.packets;

import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

/** Sends a message to a player. */
public class PlayerMessagePacket extends Packet {
    private UUID uuid;
    private Component message;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeUUID(byteBuf, uuid);
        writeString(byteBuf, GsonComponentSerializer.gson().serialize(message));
    }

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        this.uuid = readUUID(byteBuf);
        this.message = GsonComponentSerializer.gson().deserialize(readString(byteBuf));
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
    public @NotNull Component getMessage() {
        return message;
    }

    /**
     * Sets the message for the player
     *
     * @param message The message for the player
     */
    public void setMessage(@NotNull Component message) {
        this.message = message;
    }
}
