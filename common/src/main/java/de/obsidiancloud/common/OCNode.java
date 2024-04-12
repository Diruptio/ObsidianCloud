package de.obsidiancloud.common;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class OCNode {
    private final String name;
    private final String host;
    private final int port;

    public OCNode(@NotNull String name, @NotNull String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    /**
     * Checks if the node is connected to the cluster.
     *
     * @return Returns {@code true} if the node is connected to the cluster, otherwise {@code
     *     false}.
     */
    public abstract boolean isConnected();

    /**
     * Gets the servers of this node.
     *
     * @return Returns the servers of this node.
     */
    public abstract @Nullable List<OCServer> getServers();

    /**
     * Gets the name of the node.
     *
     * @return Returns the name of the node.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Gets the host of the node.
     *
     * @return Returns the host of the node.
     */
    public @NotNull String getHost() {
        return host;
    }

    /**
     * Gets the port of the node.
     *
     * @return Returns the port of the node.
     */
    public int getPort() {
        return port;
    }
}
