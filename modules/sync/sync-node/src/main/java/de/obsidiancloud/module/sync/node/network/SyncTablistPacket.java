package de.obsidiancloud.module.sync.node.network;

import de.obsidiancloud.common.network.WritablePacket;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class SyncTablistPacket extends WritablePacket {
    private Component header;
    private Component footer;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, GsonComponentSerializer.gson().serialize(this.header));
        writeString(byteBuf, GsonComponentSerializer.gson().serialize(this.footer));
    }

    public void setHeader(@NotNull Component header) {
        this.header = header;
    }

    public void setFooter(@NotNull Component footer) {
        this.footer = footer;
    }
}
