package de.obsidiancloud.common.network.listener;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerRemovedPacket;
import org.jetbrains.annotations.NotNull;

public class ServerRemovedListener implements PacketListener<ServerRemovedPacket> {
    @Override
    public void handle(@NotNull ServerRemovedPacket packet, @NotNull Connection connection) {
        OCServer server = ObsidianCloudAPI.get().getServer(packet.getServerName());
        if (server != null) {
            server.getNode().getServers().remove(server);
            // TODO: Call ServerRemovedEvent
        }
    }
}
