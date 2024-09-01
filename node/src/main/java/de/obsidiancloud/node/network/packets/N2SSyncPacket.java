package de.obsidiancloud.node.network.packets;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.network.WritablePacket;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/** A synchronization packet from a node to the server. */
public class N2SSyncPacket extends WritablePacket {
    private OCServer target;
    private List<OCNode> nodes;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        byteBuf.writeInt(nodes.size());
        for (OCNode node : nodes) {
            writeString(byteBuf, node.getName());
            List<OCServer> servers = new ArrayList<>(node.getServers());
            servers.remove(target);
            byteBuf.writeInt(node.getServers().size() - 1);
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
     * Sets the target
     *
     * @param target The target
     */
    public void setTarget(@NotNull OCServer target) {
        this.target = target;
    }

    /**
     * Sets the nodes
     *
     * @param nodes The nodes
     */
    public void setNodes(@NotNull List<OCNode> nodes) {
        this.nodes = nodes;
    }
}
