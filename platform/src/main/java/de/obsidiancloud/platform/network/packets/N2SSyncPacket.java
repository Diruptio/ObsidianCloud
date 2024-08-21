package de.obsidiancloud.platform.network.packets;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.ReadablePacket;
import de.obsidiancloud.platform.PlatformObsidianCloudAPI;
import de.obsidiancloud.platform.remote.RemoteOCNode;
import de.obsidiancloud.platform.remote.RemoteOCPlayer;
import de.obsidiancloud.platform.remote.RemoteOCServer;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/** A synchronization packet from a node to the server. */
public class N2SSyncPacket extends ReadablePacket {
    private List<RemoteOCNode> nodes;

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        PlatformObsidianCloudAPI api = (PlatformObsidianCloudAPI) ObsidianCloudAPI.get();
        nodes = new ArrayList<>();
        int nodeCount = byteBuf.readInt();
        for (int i = 0; i < nodeCount; i++) {
            String nodeName = readString(byteBuf);
            List<RemoteOCServer> servers = new ArrayList<>();
            OCNode node;
            if (nodeName.equals(api.getLocalNode().getName())) {
                node = new RemoteOCNode(nodeName, servers);
                nodes.add((RemoteOCNode) node);
            } else {
                node = api.getLocalNode();
            }
            int serverCount = byteBuf.readInt();
            for (int j = 0; j < serverCount; j++) {
                OCServer.TransferableServerData data =
                        OCServer.TransferableServerData.fromString(readString(byteBuf));
                RemoteOCServer server = new RemoteOCServer(data, node);
                int playerCount = byteBuf.readInt();
                for (int k = 0; k < playerCount; k++) {
                    server.getPlayers()
                            .add(new RemoteOCPlayer(readUUID(byteBuf), readString(byteBuf), node));
                }
                node.getServers().add(server);
            }
        }
    }

    /**
     * Gets the nodes.
     *
     * @return The nodes
     */
    public @NotNull List<RemoteOCNode> getNodes() {
        return nodes;
    }
}
