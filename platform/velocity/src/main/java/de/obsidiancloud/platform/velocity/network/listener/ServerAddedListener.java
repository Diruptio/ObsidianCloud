package de.obsidiancloud.platform.velocity.network.listener;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerAddedPacket;
import de.obsidiancloud.platform.velocity.ObsidianCloudVelocity;
import org.jetbrains.annotations.NotNull;

public class ServerAddedListener implements PacketListener<ServerAddedPacket> {
    @Override
    public void handle(@NotNull ServerAddedPacket packet, @NotNull Connection connection) {
        OCNode node = ObsidianCloudAPI.get().getNode(packet.getNode());
        if (node == null) return;
        OCServer.TransferableServerData serverData = packet.getServerData();
        if (serverData == null) return;
        if (packet.getPort() == -1) return;
        ObsidianCloudVelocity.getInstance().registerServer(serverData, node, packet.getPort());
    }
}
