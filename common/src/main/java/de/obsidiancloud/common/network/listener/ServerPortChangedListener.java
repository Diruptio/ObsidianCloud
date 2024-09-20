package de.obsidiancloud.common.network.listener;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerPortChangedPacket;
import org.jetbrains.annotations.NotNull;

/** Called when a server's port has changed. */
public class ServerPortChangedListener implements PacketListener<ServerPortChangedPacket> {
    @Override
    public void handle(@NotNull ServerPortChangedPacket packet, @NotNull Connection connection) {
        OCServer server = ObsidianCloudAPI.get().getServer(packet.getName());
        if (server != null) {
            server.updatePort(packet.getPort());
        }
    }
}
