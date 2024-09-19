package de.obsidiancloud.common.network.listener;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerUpdatedPacket;
import org.jetbrains.annotations.NotNull;

/** Called when a server's was updated. */
public class ServerUpdatedListener implements PacketListener<ServerUpdatedPacket> {
    @Override
    public void handle(@NotNull ServerUpdatedPacket packet, @NotNull Connection connection) {
        OCServer server = ObsidianCloudAPI.get().getServer(packet.getServerData().name());
        if (server != null) {
            server.updateData(packet.getServerData());
            // TODO: Call ServerUpdatedEvent
        }
    }
}
