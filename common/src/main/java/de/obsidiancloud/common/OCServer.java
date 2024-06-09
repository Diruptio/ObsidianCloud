package de.obsidiancloud.common;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represents a server in the cluster. */
public abstract class OCServer {
    private final String task;
    private final String name;
    private final Type type;
    private LifecycleState lifecycleState;
    private Status status;
    private final boolean autoStart;
    private final boolean autoDelete;
    private final String executable;
    private final int memory;
    private final List<String> jvmArgs;
    private final List<String> args;
    private final Map<String, String> environmentVariables;
    private final int port;
    private final List<OCPlayer> players;

    /**
     * Constructs a new OCServer with the specified parameters.
     *
     * @param task The task which created the server
     * @param name The name of the server
     * @param type The type of the server
     * @param lifecycleState The lifecycle state of the server
     * @param status The status of the server
     * @param autoStart Whether the server should automatically start
     * @param autoDelete Whether the server should be deleted when it is stopped
     * @param executable The java executable of the server
     * @param memory The amount of memory allocated to the server
     * @param jvmArgs The JVM arguments of the server
     * @param args The arguments of the server
     * @param environmentVariables The environment variables of the server
     * @param port The minimum port number on which the server is running
     * @param players The list of players currently connected to the server
     */
    public OCServer(
            @Nullable String task,
            @NotNull String name,
            @NotNull Type type,
            @NotNull LifecycleState lifecycleState,
            @NotNull Status status,
            boolean autoStart,
            boolean autoDelete,
            @NotNull String executable,
            int memory,
            @NotNull List<String> jvmArgs,
            @NotNull List<String> args,
            @NotNull Map<String, String> environmentVariables,
            int port,
            @NotNull List<OCPlayer> players) {
        this.task = task;
        this.name = name;
        this.type = type;
        this.lifecycleState = lifecycleState;
        this.status = status;
        this.autoStart = autoStart;
        this.autoDelete = autoDelete;
        this.executable = executable;
        this.memory = memory;
        this.jvmArgs = jvmArgs;
        this.args = args;
        this.environmentVariables = environmentVariables;
        this.port = port;
        this.players = players;
    }

    /** Starts the server. */
    public abstract void start();

    /** Stops the server. */
    public abstract void stop();

    /** Kills the server. */
    public abstract void kill();

    /**
     * Gets the node of the server.
     *
     * @return Returns the node of the server.
     */
    public abstract @NotNull OCNode getNode();

    /**
     * Gets the task which created the server.
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
     * Gets the type of the server.
     *
     * @return Returns the type of the server.
     */
    public @NotNull Type getType() {
        return type;
    }

    /**
     * Gets the lifecycle state of the server.
     *
     * @return Returns the lifecycle state of the server.
     */
    public @NotNull LifecycleState getLifecycleState() {
        return lifecycleState;
    }

    /**
     * Sets the lifecycle state of the server.
     *
     * @param lifecycleState The new lifecycle state of the server.
     */
    public void setLifecycleState(@NotNull LifecycleState lifecycleState) {
        this.lifecycleState = lifecycleState;
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
     * Gets the java executable of the server.
     *
     * @return Returns the java executable of the server.
     */
    public @NotNull String getExecutable() {
        return executable;
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
     * Gets the JVM arguments of the server.
     *
     * @return Returns the JVM arguments of the server.
     */
    public @NotNull List<String> getJvmArgs() {
        return jvmArgs;
    }

    /**
     * Gets the arguments of the server.
     *
     * @return Returns the arguments of the server.
     */
    public @NotNull List<String> getArgs() {
        return args;
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

    /** Represents the type of a server. */
    public enum Type {
        /** Represents a paper server. */
        PAPER(false, "stop"),

        /** Represents a fabric server. */
        FABRIC(false, "stop"),

        /** Represents a forge server. */
        FORGE(false, "stop"),

        /** Represents a velocity server. */
        VELOCITY(true, "shutdown");

        private final boolean proxy;
        private final String stopCommand;

        Type(boolean proxy, @NotNull String stopCommand) {
            this.proxy = proxy;
            this.stopCommand = stopCommand;
        }

        /**
         * Checks whether the server type is a proxy.
         *
         * @return Returns true if the server is a proxy, otherwise false.
         */
        public boolean isProxy() {
            return proxy;
        }

        /**
         * Gets the command to stop the server.
         *
         * @return Returns the command to stop the server.
         */
        public @NotNull String getStopCommand() {
            return stopCommand;
        }
    }

    /** Represents the lifecycle state of a server. */
    public enum LifecycleState {
        /** The server is being created. */
        CREATING,

        /** The server is online. */
        ONLINE,

        /** The server is offline. */
        OFFLINE
    }

    /** Represents the status of a server. */
    public enum Status {
        /** The server is starting. */
        STARTING,

        /** The server is running. */
        READY,

        /** The server is running. */
        NOT_READY,

        /** The server is offline. */
        OFFLINE
    }
}
