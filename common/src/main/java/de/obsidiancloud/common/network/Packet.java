package de.obsidiancloud.common.network;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A packet
 *
 * @author Miles
 * @since 02.06.2024
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public abstract class Packet {
    /** The id of the packet */
    protected String targetConnectionId;

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
     * Write an integer to the byte buffer
     *
     * @param value The value
     * @param buf The byte buffer
     */
    public static void writeVarInt(int value, @NotNull ByteBuf buf) {
        while ((value & 0xFFFFFF80) != 0L) {
            buf.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }

        buf.writeByte(value & 0x7F);
    }

    /**
     * Read an integer from the byte buffer
     *
     * @param buf The byte buffer
     * @return The value
     */
    public static int readVarInt(@NotNull ByteBuf buf) {
        int value = 0;
        int i = 0;
        int b;

        while (((b = buf.readByte()) & 0x80) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
            if (i > 35) {
                throw new IllegalArgumentException("Variable length quantity is too long");
            }
        }

        return value | (b << i);
    }

    /**
     * Write a string to the byte buffer
     *
     * @param str The value
     * @param byteBuf The byte buffer
     */
    public static void writeString(@Nullable String str, @NotNull ByteBuf byteBuf) {
        if (str == null) {
            str = "";
        }

        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        int byteLength = bytes.length;
        writeVarInt(byteLength, byteBuf);
        byteBuf.writeBytes(bytes);
    }

    /**
     * Read a string from the byte buffer
     *
     * @param byteBuf The byte buffer
     * @return The value
     */
    public static @Nullable String readString(@NotNull ByteBuf byteBuf) {
        int byteLength = readVarInt(byteBuf);
        byte[] bytes = new byte[byteLength];
        byteBuf.readBytes(bytes);

        String str = new String(bytes, StandardCharsets.UTF_8);
        return str.isEmpty() ? null : str;
    }

    /**
     * Write a UUID to the byte buffer
     *
     * @param uuid The value
     * @param byteBuf The byte buffer
     */
    public static void writeUUID(@NotNull UUID uuid, @NotNull ByteBuf byteBuf) {
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
     * @param value The value
     * @param byteBuf The byte buffer
     */
    public static void writeEnum(@NotNull Enum<?> value, @NotNull ByteBuf byteBuf) {
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
