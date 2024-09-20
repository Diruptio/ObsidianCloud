package de.obsidiancloud.common.network.listener;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerStatusChangedPacket;
import org.jetbrains.annotations.NotNull;

/** Called when a server's status was updated. */
public class ServerStatusChangedListener implements PacketListener<ServerStatusChangedPacket> {
    @Override
    public void handle(@NotNull ServerStatusChangedPacket packet, @NotNull Connection connection) {
        OCServer server = ObsidianCloudAPI.get().getServer(packet.getName());
        if (server != null) {
            server.updateStatus(packet.getStatus());
            // TODO: Call ServerStatusChangedEvent
        }
    }
}
