package de.obsidiancloud.node.remote;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RemoteOCNode extends OCNode {
    private final List<RemoteOCServer> servers;

    public RemoteOCNode(
            @NotNull String name,
            @NotNull String host,
            int port,
            @NotNull List<RemoteOCServer> servers) {
        super(name, host, port);
        this.servers = servers;
    }

    @Override
    public boolean isConnected() {
        // TODO: Check connection
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable List<OCServer> getServers() {
        return isConnected() ? (List<OCServer>) (List<?>) servers : null;
    }
}
