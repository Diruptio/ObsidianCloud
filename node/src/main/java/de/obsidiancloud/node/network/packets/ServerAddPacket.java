package de.obsidiancloud.node.network.packets;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Packet;
import de.obsidiancloud.node.local.LocalOCServer;
import de.obsidiancloud.node.remote.RemoteOCNode;
import de.obsidiancloud.node.remote.RemoteOCPlayer;
import de.obsidiancloud.node.remote.RemoteOCServer;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/** Sent from the node to a node/server when a server was added. */
public class ServerAddPacket extends Packet {
    private OCNode node;
    private OCServer server;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, node.getName());
        writeString(byteBuf, server.getTask());
        writeString(byteBuf, server.getName());
        writeString(byteBuf, server.getType().toString());
        writeString(byteBuf, server.getLifecycleState().toString());
        writeString(byteBuf, server.getStatus().toString());
        byteBuf.writeBoolean(server.isAutoStart());
        byteBuf.writeInt(server.getPlayers().size());
        for (OCPlayer player : server.getPlayers()) {
            writeUUID(byteBuf, player.getUUID());
            writeString(byteBuf, player.getName());
        }
    }

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        node = ObsidianCloudAPI.get().getNode(readString(byteBuf));
        String task = readString(byteBuf);
        String serverName = readString(byteBuf);
        OCServer.Type type = OCServer.Type.valueOf(readString(byteBuf));
        OCServer.LifecycleState lifecycleState =
                OCServer.LifecycleState.valueOf(readString(byteBuf));
        OCServer.Status status = OCServer.Status.valueOf(readString(byteBuf));
        boolean autoStart = byteBuf.readBoolean();
        int memory = byteBuf.readInt();
        int port = byteBuf.readInt();
        RemoteOCServer server =
                new RemoteOCServer(
                        serverName,
                        task,
                        type,
                        lifecycleState,
                        status,
                        autoStart,
                        memory,
                        port,
                        (RemoteOCNode) node);
        for (int k = 0; k < byteBuf.readInt(); k++) {
            server.getPlayers()
                    .add(new RemoteOCPlayer(readUUID(byteBuf), readString(byteBuf), node));
        }
    }

    /**
     * Gets the node.
     *
     * @return The node
     */
    public @NotNull RemoteOCNode getNode() {
        return (RemoteOCNode) node;
    }

    /**
     * Sets the node.
     *
     * @param node The node
     */
    public void setNode(@NotNull OCNode node) {
        this.node = node;
    }

    /**
     * Gets the server.
     *
     * @return The server
     */
    public @NotNull RemoteOCServer getServer() {
        return (RemoteOCServer) server;
    }

    /**
     * Sets the server.
     *
     * @param server The server
     */
    public void setServer(@NotNull LocalOCServer server) {
        this.server = server;
    }
}
