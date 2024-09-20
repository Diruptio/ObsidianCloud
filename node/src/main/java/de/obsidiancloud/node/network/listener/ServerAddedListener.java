package de.obsidiancloud.node.network.listener;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerAddedPacket;
import de.obsidiancloud.node.remote.RemoteOCNode;
import de.obsidiancloud.node.remote.RemoteOCServer;
import org.jetbrains.annotations.NotNull;

public class ServerAddedListener implements PacketListener<ServerAddedPacket> {
    @Override
    public void handle(@NotNull ServerAddedPacket packet, @NotNull Connection connection) {
        OCNode node = ObsidianCloudAPI.get().getNode(packet.getNode());
        if (node instanceof RemoteOCNode remoteNode
                && packet.getServerData() != null
                && packet.getServerStatus() != null) {
            RemoteOCServer server = new RemoteOCServer(packet.getServerData(), packet.getServerStatus(), remoteNode);
            node.getServers().add(server);
            // TODO: Call ServerAddedEvent
        }
    }
}
