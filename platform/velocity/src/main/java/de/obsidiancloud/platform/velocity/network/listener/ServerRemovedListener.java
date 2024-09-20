package de.obsidiancloud.platform.velocity.network.listener;

import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerRemovedPacket;
import de.obsidiancloud.platform.velocity.ObsidianCloudVelocity;
import org.jetbrains.annotations.NotNull;

public class ServerRemovedListener implements PacketListener<ServerRemovedPacket> {
    @Override
    public void handle(@NotNull ServerRemovedPacket packet, @NotNull Connection connection) {
        ObsidianCloudVelocity.getInstance().unregisterServer(packet.getServerName());
    }
}
