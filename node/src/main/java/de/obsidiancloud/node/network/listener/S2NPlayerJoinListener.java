package de.obsidiancloud.node.network.listener;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.node.NodeObsidianCloudAPI;
import de.obsidiancloud.node.network.packets.S2NPlayerJoinPacket;
import de.obsidiancloud.node.remote.RemoteOCPlayer;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class S2NPlayerJoinListener implements PacketListener<S2NPlayerJoinPacket> {
    @Override
    public void handle(@NotNull S2NPlayerJoinPacket packet, @NotNull Connection connection) {
        NodeObsidianCloudAPI api = (NodeObsidianCloudAPI) ObsidianCloudAPI.get();
        Optional<OCServer> server = api.getServer(connection);
        if (server.isPresent()) {
            UUID uuid = packet.getUUID();
            String name = packet.getName();
            server.get().getPlayers().add(new RemoteOCPlayer(uuid, name));
        }
    }
}
