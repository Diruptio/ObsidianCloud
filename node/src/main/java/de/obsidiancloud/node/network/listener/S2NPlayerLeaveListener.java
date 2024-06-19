package de.obsidiancloud.node.network.listener;

import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.node.NodeObsidianCloudAPI;
import de.obsidiancloud.node.network.packets.S2NPlayerLeavePacket;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class S2NPlayerLeaveListener implements PacketListener<S2NPlayerLeavePacket> {
    @Override
    public void handle(@NotNull S2NPlayerLeavePacket packet, @NotNull Connection connection) {
        NodeObsidianCloudAPI api = (NodeObsidianCloudAPI) ObsidianCloudAPI.get();
        OCPlayer player = ObsidianCloudAPI.get().getPlayer(packet.getUUID());
        if (player == null) return;
        System.out.println(player.getName() + " left the network.");
        // server is a Minecraft server or a Proxy or null
        Optional<OCServer> server = api.getServer(connection);
        if (server.isPresent()) {
            server.get().getPlayers().remove(player);
            // server2 is a Minecraft server and not null if server was a Proxy
            OCServer server2 = player.getProxy();
            if (server2 != null) {
                server2.getPlayers().remove(player);
            }
        }
    }
}
