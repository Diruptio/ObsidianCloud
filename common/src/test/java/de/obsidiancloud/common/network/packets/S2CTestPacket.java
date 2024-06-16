package de.obsidiancloud.common.network.packets;

import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public class S2CTestPacket extends Packet {
    public String text;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        Packet.writeString(byteBuf, text);
    }

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        text = Packet.readString(byteBuf);
    }
}
