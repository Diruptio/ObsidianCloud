package de.obsidiancloud.common;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/** Represents a node in the cluster. */
public abstract class OCNode {
    private final @NotNull String name;
    private final @NotNull String host;
    private final int port;

    /**
     * Create a new node.
     *
     * @param name The name of the node.
     * @param host The host of the node.
     * @param port The port of the node.
     */
    public OCNode(@NotNull String name, @NotNull String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    /**
     * Checks if the node is connected to the cluster.
     *
     * @return Returns true if the node is connected to the cluster, otherwise false.
     */
    public abstract boolean isConnected();

    /**
     * Gets the servers of this node.
     *
     * @return Returns the servers of this node.
     * @throws IllegalStateException If the node is not connected.
     */
    public abstract @NotNull List<OCServer> getServers();

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
