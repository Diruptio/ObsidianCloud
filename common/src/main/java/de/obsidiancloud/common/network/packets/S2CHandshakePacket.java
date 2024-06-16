package de.obsidiancloud.common.network.packets;

import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/** A handshake packet from the server to the client. */
public class S2CHandshakePacket extends Packet {
    @Override
    public void write(@NotNull ByteBuf byteBuf) {}

    @Override
    public void read(@NotNull ByteBuf byteBuf) {}
}
