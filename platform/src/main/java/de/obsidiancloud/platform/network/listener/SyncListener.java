package de.obsidiancloud.platform.network.listener;

import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.platform.PlatformObsidianCloudAPI;
import de.obsidiancloud.platform.network.packets.N2SSyncPacket;
import org.jetbrains.annotations.NotNull;

public class SyncListener implements PacketListener<N2SSyncPacket> {
    @Override
    public void handle(@NotNull N2SSyncPacket packet, @NotNull Connection connection) {
        PlatformObsidianCloudAPI api = (PlatformObsidianCloudAPI) ObsidianCloudAPI.get();
        api.getRemoteNodes().clear();
        api.getRemoteNodes().addAll(packet.getNodes());
    }
}
