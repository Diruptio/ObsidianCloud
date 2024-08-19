package de.obsidiancloud.common.network.packets;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/** Sent when a server was updated. */
public class ServerUpdatedPacket extends Packet {
    private OCServer.TransferableServerData serverData;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, serverData == null ? "" : serverData.toString());
    }

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        String serverData = readString(byteBuf);
        this.serverData =
                serverData.isEmpty()
                        ? null
                        : OCServer.TransferableServerData.fromString(readString(byteBuf));
    }

    /**
     * Gets the data of the server
     *
     * @return The data of the server
     */
    public @NotNull OCServer.TransferableServerData getServerData() {
        return serverData;
    }

    /**
     * Sets the data of the server
     *
     * @param serverData The data of the server
     */
    public void setServerData(@NotNull OCServer.TransferableServerData serverData) {
        this.serverData = serverData;
    }
}
