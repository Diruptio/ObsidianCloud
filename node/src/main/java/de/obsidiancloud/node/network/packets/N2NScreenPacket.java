package de.obsidiancloud.node.network.packets;

import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.command.CommandExecutor;
import de.obsidiancloud.common.network.Packet;
import de.obsidiancloud.node.ObsidianCloudNode;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public class N2NScreenPacket extends Packet {
    private String server;
    private CommandExecutor executor;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, server);
        byteBuf.writeBoolean(executor instanceof OCPlayer);
        if (executor instanceof OCPlayer) {
            writeUUID(byteBuf, ((OCPlayer) executor).getUUID());
        }
    }

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        server = readString(byteBuf);
        boolean isPlayer = byteBuf.readBoolean();
        if (isPlayer) {
            executor = ObsidianCloudAPI.get().getPlayer(readUUID(byteBuf));
        } else {
            executor = ObsidianCloudNode.getExecutor();
        }
    }

    public @NotNull String getServer() {
        return server;
    }

    public void setServer(@NotNull String server) {
        this.server = server;
    }

    public @NotNull CommandExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(@NotNull CommandExecutor executor) {
        this.executor = executor;
    }
}
