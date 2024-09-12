package de.obsidiancloud.platform.velocity.network.listener;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerRemovedPacket;
import de.obsidiancloud.platform.remote.RemoteOCServer;
import de.obsidiancloud.platform.velocity.ObsidianCloudVelocity;
import java.net.InetSocketAddress;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class ServerRemovedListener implements PacketListener<ServerRemovedPacket> {
    @Override
    public void handle(@NotNull ServerRemovedPacket packet, @NotNull Connection connection) {
        ProxyServer proxyServer = ObsidianCloudVelocity.getInstance().getServer();
        OCServer server = ObsidianCloudAPI.get().getServer(packet.getServerName());
        if (!(server instanceof RemoteOCServer remoteServer)) return;
        InetSocketAddress address =
                new InetSocketAddress(remoteServer.getNode().getAddress(), server.getData().port());
        Optional<RegisteredServer> registeredServer = proxyServer.getServer(server.getName());
        if (registeredServer.isPresent()) {
            ServerInfo info = registeredServer.get().getServerInfo();
            if (!info.getAddress().equals(address)) {
                proxyServer.unregisterServer(info);
            }
        }
    }
}
