package de.obsidiancloud.common;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/** Represents a node in the cluster. */
public abstract class OCNode {
    private final @NotNull String name;

    /**
     * Create a new node.
     *
     * @param name The name of the node.
     */
    public OCNode(@NotNull String name) {
        this.name = name;
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
}
