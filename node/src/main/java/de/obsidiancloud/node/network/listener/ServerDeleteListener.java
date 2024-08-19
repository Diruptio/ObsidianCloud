package de.obsidiancloud.node.network.listener;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerDeletePacket;
import de.obsidiancloud.node.remote.RemoteOCServer;
import org.jetbrains.annotations.NotNull;

public class ServerDeleteListener implements PacketListener<ServerDeletePacket> {
    @Override
    public void handle(@NotNull ServerDeletePacket packet, @NotNull Connection connection) {
        OCServer server = ObsidianCloudAPI.get().getServer(packet.getName());
        if (server == null) return;
        if (server instanceof RemoteOCServer remoteServer) {
            remoteServer.getNode().getConnection().send(packet);
        } else {
            ObsidianCloudAPI.get().deleteServer(server);
        }
    }
}
