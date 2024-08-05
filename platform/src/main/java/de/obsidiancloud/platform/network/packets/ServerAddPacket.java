package de.obsidiancloud.platform.network.packets;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.ReadablePacket;
import de.obsidiancloud.platform.remote.RemoteOCNode;
import de.obsidiancloud.platform.remote.RemoteOCPlayer;
import de.obsidiancloud.platform.remote.RemoteOCServer;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/** Sent from the node to a server when another server was added. */
public class ServerAddPacket extends ReadablePacket {
    private OCNode node;
    private OCServer server;

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
        server =
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
                    .add(
                            new RemoteOCPlayer(
                                    readUUID(byteBuf), readString(byteBuf), (RemoteOCNode) node));
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
     * Gets the server.
     *
     * @return The server
     */
    public @NotNull RemoteOCServer getServer() {
        return (RemoteOCServer) server;
    }
}
