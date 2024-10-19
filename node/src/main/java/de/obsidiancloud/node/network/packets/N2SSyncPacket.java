package de.obsidiancloud.node.network.packets;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.network.WritablePacket;
import de.obsidiancloud.node.local.LocalOCServer;
import de.obsidiancloud.node.remote.RemoteOCNode;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/** A synchronization packet from a node to the server. */
public class N2SSyncPacket extends WritablePacket {
    private List<LocalOCServer> localNodeServers;
    private List<RemoteOCNode> remoteNodes;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        byteBuf.writeInt(localNodeServers.size());
        for (LocalOCServer server : localNodeServers) {
            writeString(byteBuf, server.getData().toString());
            writeString(byteBuf, server.getStatus().toString());
            byteBuf.writeInt(server.getPort());
            if (server.getPort() != -1) {
                byteBuf.writeInt(server.getPlayers().size());
                for (OCPlayer player : server.getPlayers()) {
                    writeUUID(byteBuf, player.getUUID());
                    writeString(byteBuf, player.getName());
                }
            }
        }
        byteBuf.writeInt(remoteNodes.size());
        for (OCNode node : remoteNodes) {
            writeString(byteBuf, node.getName());
            writeString(byteBuf, node.getAddress().getHostAddress());
            List<OCServer> servers = new ArrayList<>(node.getServers());
            byteBuf.writeInt(servers.size() - 1);
            for (OCServer server : servers) {
                writeString(byteBuf, server.getData().toString());
                writeString(byteBuf, server.getStatus().toString());
                byteBuf.writeInt(server.getPlayers().size());
                for (OCPlayer player : server.getPlayers()) {
                    writeUUID(byteBuf, player.getUUID());
                    writeString(byteBuf, player.getName());
                }
            }
        }
    }

    /**
     * Sets the local node
     *
     * @param localNodeServers The local node's servers
     */
    public void setLocalNodeServers(@NotNull List<LocalOCServer> localNodeServers) {
        this.localNodeServers = localNodeServers;
    }

    /**
     * Sets the remote nodes
     *
     * @param remoteNodes The remote nodes
     */
    public void setRemoteNodes(@NotNull List<RemoteOCNode> remoteNodes) {
        this.remoteNodes = remoteNodes;
    }
}
