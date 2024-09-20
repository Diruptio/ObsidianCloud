package de.obsidiancloud.node.remote;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.network.Connection;
import java.net.InetAddress;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class RemoteOCNode extends OCNode {
    private Connection connection = null;
    private final List<RemoteOCServer> servers;

    public RemoteOCNode(@NotNull String name, @NotNull InetAddress address, @NotNull List<RemoteOCServer> servers) {
        super(name, address);
        this.servers = servers;
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
    @SuppressWarnings("unchecked")
    public @NotNull List<OCServer> getServers() {
        if (isConnected()) {
            return (List<OCServer>) (List<?>) servers;
        } else {
            throw new IllegalStateException("The connection is not established yet.");
        }
    }
}
