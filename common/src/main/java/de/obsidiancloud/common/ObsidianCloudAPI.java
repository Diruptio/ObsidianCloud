package de.obsidiancloud.common;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ObsidianCloudAPI {
    private static ObsidianCloudAPI instance = null;

    /**
     * Gets a list of all nodes.
     *
     * @return A list of all nodes.
     */
    public abstract @NotNull List<OCNode> getNodes();

    /**
     * Gets a node by its name.
     *
     * @param name The name of the node.
     * @return The node with the given name.
     */
    public @Nullable OCNode getNode(@NotNull String name) {
        for (OCNode node : getNodes()) {
            if (node.getName().equalsIgnoreCase(name)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Gets all connected nodes.
     *
     * @return A list of all connected nodes.
     */
    public @NotNull List<OCNode> getConnectedNodes() {
        return getNodes().stream().filter(OCNode::isConnected).toList();
    }

    /**
     * Gets the local node.
     *
     * @return The local node.
     * @throws IllegalStateException If the local node is not found.
     */
    public abstract @NotNull OCNode getLocalNode();

    /**
     * Gets a list of all tasks.
     *
     * @return A list of all tasks.
     */
    public abstract @NotNull List<OCTask> getTasks();

    /**
     * Gets a task by its name.
     *
     * @param name The name of the task.
     * @return The task with the given name.
     */
    public @Nullable OCTask getTask(@NotNull String name) {
        for (OCTask task : getTasks()) {
            if (task.name().equalsIgnoreCase(name)) {
                return task;
            }
        }
        return null;
    }

    /**
     * Gets a list of all servers.
     *
     * @return A list of all servers.
     */
    public abstract @NotNull List<OCServer> getServers();

    /**
     * Gets a server by its name.
     *
     * @param name The name of the server.
     * @return The server with the given name.
     */
    public @Nullable OCServer getServer(@NotNull String name) {
        for (OCServer server : getServers()) {
            if (server.getName().equalsIgnoreCase(name)) {
                return server;
            }
        }
        return null;
    }

    /**
     * Gets the local server.
     *
     * @return The local server.
     * @throws IllegalStateException If the local server is not found.
     */
    public @NotNull OCServer getLocalServer() {
        throw new IllegalStateException("Local server not found!");
    }

    /**
     * Get the ObsidianCloudAPI.
     *
     * @return The ObsidianCloudAPI.
     */
    public static @NotNull ObsidianCloudAPI get() {
        if (instance == null) {
            throw new IllegalStateException("ObsidianCloudAPI is not initialized yet!");
        } else {
            return instance;
        }
    }

    /**
     * Set the instance of the ObsidianCloudAPI.
     *
     * @param instance The instance of the ObsidianCloudAPI.
     */
    public static void setInstance(@NotNull ObsidianCloudAPI instance) {
        ObsidianCloudAPI.instance = instance;
    }
}
