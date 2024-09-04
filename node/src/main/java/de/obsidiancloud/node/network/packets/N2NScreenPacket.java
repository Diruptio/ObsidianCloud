package de.obsidiancloud.node.network.packets;

import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.command.CommandExecutor;
import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public class N2NScreenPacket extends Packet {
    private String serverName;
    private CommandExecutor executor;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, serverName);
        byteBuf.writeBoolean(executor instanceof OCPlayer);
        if (executor instanceof OCPlayer) {
            writeUUID(byteBuf, ((OCPlayer) executor).getUUID());
        }
    }

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        serverName = readString(byteBuf);
        boolean isPlayer = byteBuf.readBoolean();
        if (isPlayer) {
            executor = ObsidianCloudAPI.get().getPlayer(readUUID(byteBuf));
        }
    }
}
