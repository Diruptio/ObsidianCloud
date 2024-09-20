package de.obsidiancloud.node.network.listener;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerUpdatePacket;
import de.obsidiancloud.common.network.packets.ServerUpdatedPacket;
import de.obsidiancloud.node.ObsidianCloudNode;
import de.obsidiancloud.node.remote.RemoteOCServer;
import org.jetbrains.annotations.NotNull;

public class ServerUpdateListener implements PacketListener<ServerUpdatePacket> {
    @Override
    public void handle(@NotNull ServerUpdatePacket packet, @NotNull Connection connection) {
        OCServer server =
                ObsidianCloudAPI.get().getServer(packet.getServerData().name());
        if (server instanceof RemoteOCServer remoteServer) {
            remoteServer.getNode().getConnection().send(packet);
        } else if (server != null) {
            server.updateData(packet.getServerData());
            ServerUpdatedPacket response = new ServerUpdatedPacket();
            response.setServerData(server.getData());
            for (Connection con : ObsidianCloudNode.getNetworkServer().getConnections()) {
                con.send(response);
            }
            // TODO: Call ServerUpdatedEvent
        }
    }
}
