package de.obsidiancloud.platform.velocity.network.listener;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.platform.network.packets.N2SSyncPacket;
import de.obsidiancloud.platform.remote.RemoteOCNode;
import de.obsidiancloud.platform.velocity.ObsidianCloudVelocity;
import org.jetbrains.annotations.NotNull;

public class SyncListener implements PacketListener<N2SSyncPacket> {
    @Override
    public void handle(@NotNull N2SSyncPacket packet, @NotNull Connection connection) {
        for (OCServer server : packet.getLocalNodeServers()) {
            ObsidianCloudVelocity.getInstance().registerServer(server.getData(), server.getNode(), server.getPort());
        }
        for (RemoteOCNode node : packet.getRemoteNodes()) {
            if (node.isConnected()) {
                for (OCServer server : node.getServers()) {
                    ObsidianCloudVelocity.getInstance()
                            .registerServer(server.getData(), server.getNode(), server.getPort());
                }
            }
        }
    }
}
