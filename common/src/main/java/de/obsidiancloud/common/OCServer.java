package de.obsidiancloud.common;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class OCServer {
    private final String task;
    private final String name;
    private Status status;
    private final Type type;
    private final int port;
    private final List<OCPlayer> players;
    private final int maxPlayers;
    private final boolean autoStart;
    private final boolean autoDelete;
    private final int memory;
    private final Map<String, String> environmentVariables;
    private final boolean maintenance;

    /**
     * Constructs a new OCServer with the specified parameters.
     *
     * @param task The list of tasks of the server
     * @param name The name of the server
     * @param status The status of the server
     * @param type The type of the server
     * @param port The port number on which the server is running
     * @param players The list of players currently connected to the server
     * @param maxPlayers The maximum number of players that can connect to the server
     * @param autoStart Whether the server should automatically start
     * @param autoDelete Whether the server should be deleted when it is stopped
     * @param memory The amount of memory allocated to the server
     * @param environmentVariables The environment variables of the server
     * @param maintenance Whether the server is currently in maintenance mode
     */
    public OCServer(
            @Nullable String task,
            @NotNull String name,
            @NotNull Status status,
            @NotNull Type type,
            int port,
            @NotNull List<OCPlayer> players,
            int maxPlayers,
            boolean autoStart,
            boolean autoDelete,
            int memory,
            Map<String, String> environmentVariables,
            boolean maintenance) {
        this.task = task;
        this.name = name;
        this.status = status;
        this.type = type;
        this.port = port;
        this.players = players;
        this.maxPlayers = maxPlayers;
        this.autoStart = autoStart;
        this.autoDelete = autoDelete;
        this.memory = memory;
        this.environmentVariables = environmentVariables;
        this.maintenance = maintenance;
    }

    /** Starts the server. */
    public abstract void start();

    /** Stops the server. */
    public abstract void stop();

    /**
     * Gets the node of the server.
     *
     * @return Returns the node of the server.
     */
    public abstract @NotNull OCNode getNode();

    /**
     * Gets the task of the server.
     *
     * @return Returns the task of the server.
     */
    public @Nullable String getTask() {
        return task;
    }

    /**
     * Gets the name of the server.
     *
     * @return Returns the name of the server.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Gets the status of the server.
     *
     * @return Returns the status of the server.
     */
    public @NotNull Status getStatus() {
        return status;
    }

    /**
     * Sets the status of the server.
     *
     * @param status The new status of the server.
     */
    public void setStatus(@NotNull Status status) {
        this.status = status;
    }

    /**
     * Gets the type of the server.
     *
     * @return Returns the type of the server.
     */
    public @NotNull Type getType() {
        return type;
    }

    /**
     * Gets the port number on which the server is running.
     *
     * @return Returns the port number on which the server is running.
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the list of players currently connected to the server.
     *
     * @return Returns the list of players currently connected to the server.
     */
    public @NotNull List<OCPlayer> getPlayers() {
        return players;
    }

    /**
     * Gets the maximum number of players that can connect to the server.
     *
     * @return Returns the maximum number of players that can connect to the server.
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Checks whether the server should automatically start.
     *
     * @return Returns whether the server should automatically start.
     */
    public boolean isAutoStart() {
        return autoStart;
    }

    /**
     * Checks whether the server should be deleted when it is stopped.
     *
     * @return Returns whether the server should be deleted when it is stopped.
     */
    public boolean isAutoDelete() {
        return autoDelete;
    }

    /**
     * Gets the amount of memory allocated to the server.
     *
     * @return Returns the amount of memory allocated to the server.
     */
    public int getMemory() {
        return memory;
    }

    /**
     * Gets the environment variables of the server.
     *
     * @return Returns the environment variables of the server.
     */
    public @NotNull Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    /**
     * Checks whether the server is currently in maintenance mode.
     *
     * @return Returns whether the server is currently in maintenance mode.
     */
    public boolean isMaintenance() {
        return maintenance;
    }

    public static enum Type {
        BUKKIT(false),
        FABRIC(false),
        FORGE(false),
        BUNGEECORD(true),
        VELOCITY(true);

        private final boolean proxy;

        Type(boolean proxy) {
            this.proxy = proxy;
        }

        public boolean isProxy() {
            return proxy;
        }
    }

    public static enum Status {
        LOADING,
        ONLINE,
        OFFLINE
    }
}
