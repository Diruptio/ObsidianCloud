package de.obsidiancloud.node.network.listener;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.listener.*;
import de.obsidiancloud.node.NodeObsidianCloudAPI;
import de.obsidiancloud.node.ObsidianCloudNode;
import de.obsidiancloud.node.local.LocalOCServer;
import de.obsidiancloud.node.network.packets.N2SSyncPacket;
import de.obsidiancloud.node.network.packets.S2NHandshakePacket;
import org.jetbrains.annotations.NotNull;

public class S2NHandshakeListener implements PacketListener<S2NHandshakePacket> {
    @Override
    public void handle(@NotNull S2NHandshakePacket packet, @NotNull Connection connection) {
        if (packet.getClusterKey().equals(ObsidianCloudNode.getClusterKey().get())) {
            connection.removePacketListener(this);
            ObsidianCloudNode.getNetworkServer().getConnections().add(connection);
            for (OCServer server : ObsidianCloudAPI.get().getLocalNode().getServers()) {
                if (packet.getName().equals(server.getName())) {
                    ((LocalOCServer) server).setConnection(connection);

                    // common
                    connection.addPacketListener(new PlayerKickListener());
                    connection.addPacketListener(new PlayerMessageListener());
                    connection.addPacketListener(new ServerPortChangedListener());
                    connection.addPacketListener(new ServerRemovedListener());
                    connection.addPacketListener(new ServerStatusChangedListener());
                    connection.addPacketListener(new ServerUpdatedListener());

                    // node
                    connection.addPacketListener(new N2NScreenListener());
                    connection.addPacketListener(new S2NPlayerJoinListener());
                    connection.addPacketListener(new S2NPlayerLeaveListener());
                    connection.addPacketListener(new ServerAddedListener());
                    connection.addPacketListener(new ServerCreateListener());
                    connection.addPacketListener(new ServerDeleteListener());
                    connection.addPacketListener(new ServerStatusChangeListener());
                    connection.addPacketListener(new ServerUpdateListener());

                    N2SSyncPacket syncPacket = new N2SSyncPacket();
                    syncPacket.setLocalNodeServers(
                            ((NodeObsidianCloudAPI) ObsidianCloudAPI.get())
                                    .getLocalNode()
                                    .getLocalServers());
                    syncPacket.setRemoteNodes(
                            ((NodeObsidianCloudAPI) ObsidianCloudAPI.get()).getRemoteNodes());
                    connection.send(syncPacket);
                    break;
                }
            }
        } else {
            connection.close();
        }
    }
}
