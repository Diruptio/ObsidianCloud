package de.obsidiancloud.node.network.listener;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.command.CommandExecutor;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.node.local.LocalOCServer;
import de.obsidiancloud.node.network.packets.N2NScreenPacket;
import de.obsidiancloud.node.remote.RemoteOCServer;
import org.jetbrains.annotations.NotNull;

public class N2NScreenListener implements PacketListener<N2NScreenPacket> {
    @Override
    public void handle(@NotNull N2NScreenPacket packet, @NotNull Connection connection) {
        OCServer server = ObsidianCloudAPI.get().getServer(packet.getServer());
        CommandExecutor executor = packet.getExecutor();
        if (server instanceof LocalOCServer localServer) {
            if (localServer.getScreenReaders().contains(executor)) {
                localServer.getScreenReaders().remove(executor);
            } else {
                localServer.getScreenReaders().add(executor);
            }
        } else if (server instanceof RemoteOCServer remoteServer) {
            remoteServer.getNode().getConnection().send(packet);
        }
    }
}
