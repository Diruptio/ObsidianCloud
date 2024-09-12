package de.obsidiancloud.platform.velocity.network.listener;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerAddedPacket;
import de.obsidiancloud.platform.velocity.ObsidianCloudVelocity;
import java.net.InetSocketAddress;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class ServerAddedListener implements PacketListener<ServerAddedPacket> {
    @Override
    public void handle(@NotNull ServerAddedPacket packet, @NotNull Connection connection) {
        ProxyServer proxyServer = ObsidianCloudVelocity.getInstance().getServer();
        OCNode node = ObsidianCloudAPI.get().getNode(packet.getNode());
        if (node == null) return;
        OCServer.TransferableServerData serverData = packet.getServerData();
        if (serverData == null) return;
        InetSocketAddress address =
                new InetSocketAddress(node.getAddress(), packet.getServerData().port());
        Optional<RegisteredServer> registeredServer =
                proxyServer.getServer(packet.getServerData().name());
        if (registeredServer.isPresent()) {
            ServerInfo info = registeredServer.get().getServerInfo();
            if (!info.getAddress().equals(address)) {
                proxyServer.unregisterServer(info);
            }
        }
        proxyServer.registerServer(new ServerInfo(packet.getServerData().name(), address));
    }
}
