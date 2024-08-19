package de.obsidiancloud.node.network.listener;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.listener.PlayerKickListener;
import de.obsidiancloud.common.network.listener.PlayerMessageListener;
import de.obsidiancloud.common.network.listener.ServerRemovedListener;
import de.obsidiancloud.common.network.listener.ServerUpdatedListener;
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
            for (OCServer server : ObsidianCloudAPI.get().getLocalNode().getServers()) {
                if (packet.getName().equals(server.getName())) {
                    ((LocalOCServer) server).setConnection(connection);
                    connection.addPacketListener(new PlayerKickListener());
                    connection.addPacketListener(new PlayerMessageListener());
                    connection.addPacketListener(new S2NPlayerJoinListener());
                    connection.addPacketListener(new S2NPlayerLeaveListener());
                    connection.addPacketListener(new ServerAddedListener());
                    connection.addPacketListener(new ServerCreateListener());
                    connection.addPacketListener(new ServerDeleteListener());
                    connection.addPacketListener(new ServerRemovedListener());
                    connection.addPacketListener(new ServerUpdateListener());
                    connection.addPacketListener(new ServerUpdatedListener());
                    N2SSyncPacket syncPacket = new N2SSyncPacket();
                    syncPacket.setTarget(server);
                    syncPacket.setNodes(ObsidianCloudAPI.get().getNodes());
                    connection.send(syncPacket);
                    break;
                }
            }
        } else {
            connection.close();
        }
    }
}
