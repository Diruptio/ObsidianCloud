package de.obsidiancloud.node.network.listener;

import de.obsidiancloud.common.OCTask;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerAddedPacket;
import de.obsidiancloud.common.network.packets.ServerCreatePacket;
import org.jetbrains.annotations.NotNull;

public class ServerCreateListener implements PacketListener<ServerCreatePacket> {
    @Override
    public void handle(@NotNull ServerCreatePacket packet, @NotNull Connection connection) {
        OCTask task = ObsidianCloudAPI.get().getTask(packet.getTask());
        if (task == null) {
            ServerAddedPacket response = new ServerAddedPacket();
            response.setNode(ObsidianCloudAPI.get().getLocalNode().getName());
            response.setServerData(null);
            response.setServerStatus(null);
            connection.send(response);
        } else {
            ObsidianCloudAPI.get().createServer(task);
        }
    }
}
