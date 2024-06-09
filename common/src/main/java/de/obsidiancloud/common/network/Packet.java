package de.obsidiancloud.common.network;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Miles
 * @since 02.06.2024
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public abstract class Packet {

    protected String targetConnectionId;

    public abstract void write(ByteBuf byteBuf);

    public abstract void read(ByteBuf byteBuf);

    public static void writeVarInt(int value, ByteBuf buf) {
        while ((value & 0xFFFFFF80) != 0L) {
            buf.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }

        buf.writeByte(value & 0x7F);
    }

    public static int readVarInt(ByteBuf buf) {
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

    public static void writeString(String s, ByteBuf byteBuf) {
        if (s == null) {
            s = "";
        }

        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        int byteLength = bytes.length;
        writeVarInt(byteLength, byteBuf);
        byteBuf.writeBytes(bytes);
    }

    public static String readString(ByteBuf byteBuf) {
        int byteLength = readVarInt(byteBuf);
        byte[] bytes = new byte[byteLength];
        byteBuf.readBytes(bytes);

        String s = new String(bytes, StandardCharsets.UTF_8);
        return s.equals("") ? null : s;
    }

    public static void writeUUID(UUID uuid, ByteBuf byteBuf) {
        byteBuf.writeLong(uuid.getMostSignificantBits());
        byteBuf.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteBuf byteBuf) {
        long most = byteBuf.readLong();
        long least = byteBuf.readLong();
        return new UUID(most, least);
    }

    public static void writeEnum(Enum<?> value, ByteBuf byteBuf) {
        byteBuf.writeByte(value.ordinal());
    }

    public static <T> T readEnum(ByteBuf byteBuf, T[] values) {
        int ordinal = byteBuf.readByte();
        if (values.length <= ordinal) {
            throw new RuntimeException("No Enum found");
        }

        return values[ordinal];
    }
}
