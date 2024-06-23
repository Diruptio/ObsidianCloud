package de.obsidiancloud.common.network.packets;

import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/** A custom message. */
public class CustomMessagePacket extends Packet {
    private String channel;
    private byte[] message;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, channel);
        byteBuf.writeInt(message.length);
        byteBuf.writeBytes(message);
    }

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        channel = readString(byteBuf);
        message = new byte[byteBuf.readInt()];
        byteBuf.readBytes(message);
    }

    /**
     * Gets The channel for the message
     *
     * @return The channel for the message
     */
    public @NotNull String getChannel() {
        return channel;
    }

    /**
     * Sets The channel for the message
     *
     * @param channel The channel for the message
     */
    public void setChannel(@NotNull String channel) {
        this.channel = channel;
    }

    /**
     * Gets The message
     *
     * @return The message
     */
    public byte[] getMessage() {
        return message;
    }

    /**
     * Sets The message
     *
     * @param message The message
     */
    public void setMessage(byte[] message) {
        this.message = message;
    }
}
