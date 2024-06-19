package de.obsidiancloud.common.network;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/** A packet that only can be read from. */
public abstract class ReadablePacket extends Packet {
    public final void write(@NotNull ByteBuf byteBuf) {
        throw new UnsupportedOperationException("Packet is not writable");
    }
}
