package de.obsidiancloud.node.network.listener;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerStatusChangePacket;
import org.jetbrains.annotations.NotNull;

public class ServerStatusChangeListener implements PacketListener<ServerStatusChangePacket> {
    @Override
    public void handle(@NotNull ServerStatusChangePacket packet, @NotNull Connection connection) {
        OCServer server = ObsidianCloudAPI.get().getServer(packet.getName());
        if (server != null) {
            server.setStatus(packet.getStatus());
        }
    }
}
