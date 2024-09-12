package de.obsidiancloud.platform.velocity.network.listener;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.platform.network.packets.N2SSyncPacket;
import de.obsidiancloud.platform.remote.RemoteOCNode;
import de.obsidiancloud.platform.remote.RemoteOCServer;
import de.obsidiancloud.platform.velocity.ObsidianCloudVelocity;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class SyncListener implements PacketListener<N2SSyncPacket> {
    @Override
    public void handle(@NotNull N2SSyncPacket packet, @NotNull Connection connection) {
        ProxyServer proxyServer = ObsidianCloudVelocity.getInstance().getServer();
        List<RemoteOCServer> servers = new ArrayList<>(packet.getLocalNodeServers());
        for (RemoteOCNode node : packet.getRemoteNodes()) {
            if (node.isConnected()) {
                for (OCServer server : node.getServers()) {
                    servers.add((RemoteOCServer) server);
                }
            }
        }
        for (RemoteOCServer server : servers) {
            InetSocketAddress address =
                    new InetSocketAddress(server.getNode().getAddress(), server.getData().port());
            Optional<RegisteredServer> registeredServer = proxyServer.getServer(server.getName());
            if (registeredServer.isPresent()) {
                ServerInfo info = registeredServer.get().getServerInfo();
                if (!info.getAddress().equals(address)) {
                    proxyServer.unregisterServer(info);
                }
            }
            proxyServer.registerServer(new ServerInfo(server.getName(), address));
        }
    }
}
