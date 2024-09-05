package de.obsidiancloud.node.network.listener;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.command.CommandExecutor;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.node.local.LocalOCServer;
import de.obsidiancloud.node.network.packets.N2NScreenPacket;
import de.obsidiancloud.node.remote.RemoteOCServer;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class N2NScreenListener implements PacketListener<N2NScreenPacket> {
    @Override
    public void handle(@NotNull N2NScreenPacket packet, @NotNull Connection connection) {
        OCServer server = ObsidianCloudAPI.get().getServer(packet.getServer());
        CommandExecutor executor = packet.getExecutor();
        if (server instanceof LocalOCServer localServer) {
            Set<CommandExecutor> screenReaders = localServer.getScreenReaders();
            if (screenReaders.contains(executor)) {
                screenReaders.remove(executor);
                executor.sendMessage(
                        "§aScreen mirroring of §e" + server.getName() + " §ahas been disabled.");
            } else {
                screenReaders.add(executor);
                executor.sendMessage(
                        "§aScreen mirroring of §e" + server.getName() + " §ahas been enabled.");
            }
        } else if (server instanceof RemoteOCServer remoteServer) {
            remoteServer.getNode().getConnection().send(packet);
        }
    }
}
