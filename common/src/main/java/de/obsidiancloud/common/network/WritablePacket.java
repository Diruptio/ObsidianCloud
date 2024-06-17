package de.obsidiancloud.common.network;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/** A packet that only can be written to. */
public abstract class WritablePacket extends Packet {
    public final void read(@NotNull ByteBuf byteBuf) {
        throw new UnsupportedOperationException("Packet is not readable");
    }
}
