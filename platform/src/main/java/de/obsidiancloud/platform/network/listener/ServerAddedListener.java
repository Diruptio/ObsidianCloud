package de.obsidiancloud.platform.network.listener;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerAddedPacket;
import de.obsidiancloud.platform.remote.RemoteOCServer;
import org.jetbrains.annotations.NotNull;

public class ServerAddedListener implements PacketListener<ServerAddedPacket> {
    @Override
    public void handle(@NotNull ServerAddedPacket packet, @NotNull Connection connection) {
        OCNode node = ObsidianCloudAPI.get().getNode(packet.getNode());
        if (node != null && packet.getServerData() != null) {
            RemoteOCServer server = new RemoteOCServer(packet.getServerData(), node);
            node.getServers().add(server);
            // TODO: Call ServerAddedEvent
        }
    }
}
