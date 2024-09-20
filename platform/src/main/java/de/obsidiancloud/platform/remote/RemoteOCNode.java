package de.obsidiancloud.platform.remote;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import java.net.InetAddress;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class RemoteOCNode extends OCNode {
    private boolean connected;
    private final List<RemoteOCServer> servers;

    public RemoteOCNode(@NotNull String name, @NotNull InetAddress address, @NotNull List<RemoteOCServer> servers) {
        super(name, address);
        this.servers = servers;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    public void updateConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull List<OCServer> getServers() {
        if (isConnected()) {
            return (List<OCServer>) (List<?>) servers;
        } else {
            throw new IllegalStateException("Node is not connected!");
        }
    }
}
