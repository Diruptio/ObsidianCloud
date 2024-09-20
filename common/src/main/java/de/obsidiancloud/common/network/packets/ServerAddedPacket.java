package de.obsidiancloud.common.network.packets;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Sent when a server was created. */
public class ServerAddedPacket extends Packet {
    private String node;
    private OCServer.TransferableServerData serverData;
    private OCServer.Status serverStatus;
    private int port;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, node);
        writeString(byteBuf, serverData == null ? "" : serverData.toString());
        writeString(byteBuf, serverStatus == null ? "" : serverStatus.toString());
        byteBuf.writeInt(port);
    }

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        node = readString(byteBuf);
        String serverData = readString(byteBuf);
        this.serverData = serverData.isEmpty() ? null : OCServer.TransferableServerData.fromString(serverData);
        String serverStatus = readString(byteBuf);
        this.serverStatus = serverStatus.isEmpty() ? null : OCServer.Status.valueOf(serverStatus);
        port = byteBuf.readInt();
    }

    /**
     * Gets the name of the node
     *
     * @return The name
     */
    public @NotNull String getNode() {
        return node;
    }

    /**
     * Sets the name of the node
     *
     * @param node The name
     */
    public void setNode(@NotNull String node) {
        this.node = node;
    }

    /**
     * Gets the data of the server
     *
     * @return The data
     */
    public @Nullable OCServer.TransferableServerData getServerData() {
        return serverData;
    }

    /**
     * Sets the data of the server
     *
     * @param serverData The data
     */
    public void setServerData(@Nullable OCServer.TransferableServerData serverData) {
        this.serverData = serverData;
    }

    /**
     * Gets the status of the server
     *
     * @return The status
     */
    public @Nullable OCServer.Status getServerStatus() {
        return serverStatus;
    }

    /**
     * Sets the status of the server
     *
     * @param serverStatus The status
     */
    public void setServerStatus(@Nullable OCServer.Status serverStatus) {
        this.serverStatus = serverStatus;
    }

    /**
     * Gets the port of the server
     *
     * @return The port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port of the server
     *
     * @param port The port
     */
    public void setPort(int port) {
        this.port = port;
    }
}
