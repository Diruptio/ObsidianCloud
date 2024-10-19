package de.obsidiancloud.platform.network.packets;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.ReadablePacket;
import de.obsidiancloud.platform.PlatformObsidianCloudAPI;
import de.obsidiancloud.platform.remote.RemoteOCNode;
import de.obsidiancloud.platform.remote.RemoteOCPlayer;
import de.obsidiancloud.platform.remote.RemoteOCServer;
import io.netty.buffer.ByteBuf;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/** A synchronization packet from a node to the server. */
public class N2SSyncPacket extends ReadablePacket {
    private List<RemoteOCServer> localNodeServers;
    private List<RemoteOCNode> remoteNodes;

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        PlatformObsidianCloudAPI api = (PlatformObsidianCloudAPI) ObsidianCloudAPI.get();
        localNodeServers = new ArrayList<>();
        int localNodeServerCount = byteBuf.readInt();
        for (int i = 0; i < localNodeServerCount; i++) {
            OCServer.TransferableServerData data = OCServer.TransferableServerData.fromString(readString(byteBuf));
            OCServer.Status status = OCServer.Status.valueOf(readString(byteBuf));
            RemoteOCServer server = new RemoteOCServer(data, status, api.getLocalNode());
            int port = byteBuf.readInt();
            server.updatePort(byteBuf.readInt());
            if (port != -1) {
                int playerCount = byteBuf.readInt();
                for (int j = 0; j < playerCount; j++) {
                    server.getPlayers().add(new RemoteOCPlayer(readUUID(byteBuf), readString(byteBuf)));
                }
            }
            localNodeServers.add(server);
        }
        remoteNodes = new ArrayList<>();
        int nodeCount = byteBuf.readInt();
        for (int i = 0; i < nodeCount; i++) {
            String nodeName = readString(byteBuf);
            InetAddress address;
            try {
                address = InetAddress.getByName(readString(byteBuf));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            List<RemoteOCServer> servers = new ArrayList<>();
            RemoteOCNode node = new RemoteOCNode(nodeName, address, servers);
            remoteNodes.add(node);
            int serverCount = byteBuf.readInt();
            for (int j = 0; j < serverCount; j++) {
                OCServer.TransferableServerData data = OCServer.TransferableServerData.fromString(readString(byteBuf));
                OCServer.Status status = OCServer.Status.valueOf(readString(byteBuf));
                RemoteOCServer server = new RemoteOCServer(data, status, node);
                int port = byteBuf.readInt();
                server.updatePort(port);
                if (port != -1) {
                    int playerCount = byteBuf.readInt();
                    for (int k = 0; k < playerCount; k++) {
                        server.getPlayers().add(new RemoteOCPlayer(readUUID(byteBuf), readString(byteBuf)));
                    }
                }
                node.getServers().add(server);
            }
        }
    }

    /**
     * Gets the local node's servers.
     *
     * @return The local node's servers
     */
    public @NotNull List<RemoteOCServer> getLocalNodeServers() {
        return localNodeServers;
    }

    /**
     * Gets the nodes.
     *
     * @return The nodes
     */
    public @NotNull List<RemoteOCNode> getRemoteNodes() {
        return remoteNodes;
    }
}
