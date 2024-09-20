package de.obsidiancloud.platform.remote;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.platform.local.LocalOCServer;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class RemoteLocalOCNode extends OCNode {
    private Connection connection = null;
    private final LocalOCServer localServer;
    private final List<RemoteOCServer> remoteServers = new ArrayList<>();

    public RemoteLocalOCNode(
            @NotNull String name,
            @NotNull InetAddress address,
            @NotNull LocalOCServer localServer) {
        super(name, address);
        this.localServer = localServer;
    }

    public @NotNull LocalOCServer getLocalServer() {
        return localServer;
    }

    @Override
    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    public @NotNull Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("The connection is not established yet.");
        } else {
            return connection;
        }
    }

    public void setConnection(@NotNull Connection connection) {
        this.connection = connection;
    }

    @Override
    public @NotNull List<OCServer> getServers() {
        if (isConnected()) {
            List<OCServer> servers = new ArrayList<>();
            servers.add(localServer);
            servers.addAll(remoteServers);
            return servers;
        } else {
            throw new IllegalStateException("Node is not connected!");
        }
    }
}
