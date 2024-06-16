package de.obsidiancloud.common.network;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Packet {
    /**
     * Write the packet to the byte buffer
     *
     * @param byteBuf The byte buffer
     */
    public abstract void write(@NotNull ByteBuf byteBuf);

    /**
     * Read the packet from the byte buffer
     *
     * @param byteBuf The byte buffer
     */
    public abstract void read(@NotNull ByteBuf byteBuf);

    /**
     * Write a string to the byte buffer
     *
     * @param byteBuf The byte buffer
     * @param str The value
     */
    public static void writeString(@NotNull ByteBuf byteBuf, @Nullable String str) {
        if (str == null) str = "";
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    /**
     * Read a string from the byte buffer
     *
     * @param byteBuf The byte buffer
     * @return The value
     */
    public static @NotNull String readString(@NotNull ByteBuf byteBuf) {
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Write a UUID to the byte buffer
     *
     * @param byteBuf The byte buffer
     * @param uuid The value
     */
    public static void writeUUID(@NotNull ByteBuf byteBuf, @NotNull UUID uuid) {
        byteBuf.writeLong(uuid.getMostSignificantBits());
        byteBuf.writeLong(uuid.getLeastSignificantBits());
    }

    /**
     * Read a UUID from the byte buffer
     *
     * @param byteBuf The byte buffer
     * @return The value
     */
    public static @NotNull UUID readUUID(@NotNull ByteBuf byteBuf) {
        long most = byteBuf.readLong();
        long least = byteBuf.readLong();
        return new UUID(most, least);
    }

    /**
     * Write an enum to the byte buffer
     *
     * @param byteBuf The byte buffer
     * @param value The value
     */
    public static void writeEnum(@NotNull ByteBuf byteBuf, @NotNull Enum<?> value) {
        byteBuf.writeByte(value.ordinal());
    }

    /**
     * Read an enum from the byte buffer
     *
     * @param <T> The type of the enum
     * @param byteBuf The byte buffer
     * @param values The values
     * @return The value
     */
    public static <T> @NotNull T readEnum(@NotNull ByteBuf byteBuf, @NotNull T[] values) {
        int ordinal = byteBuf.readByte();
        if (values.length <= ordinal) {
            throw new RuntimeException("No Enum found");
        }
        return values[ordinal];
    }
}
