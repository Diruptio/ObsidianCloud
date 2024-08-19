package de.obsidiancloud.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represents a server in the cluster. */
public abstract class OCServer {
    private @NotNull TransferableServerData data;
    private final @NotNull Set<OCPlayer> players;

    /**
     * Create a new OCServer with the specified parameters.
     *
     * @param data The data of the server
     * @param players The list of players currently connected to the server
     */
    public OCServer(@NotNull TransferableServerData data, @NotNull Set<OCPlayer> players) {
        this.data = data;
        this.players = players;
    }

    /** Starts the server. */
    public abstract void start();

    /** Stops the server. */
    public abstract void stop();

    /** Kills the server. */
    public abstract void kill();

    /**
     * Sets the lifecycle state of the server.
     *
     * @param lifecycleState The lifecycle state of the server
     */
    public abstract void setLifecycleState(@NotNull LifecycleState lifecycleState);

    /**
     * Sets the status of the server.
     *
     * @param status The status of the server
     */
    public abstract void setStatus(@NotNull Status status);

    /**
     * Gets the node of the server.
     *
     * @return Returns the node of the server.
     */
    public abstract @NotNull OCNode getNode();

    /**
     * Gets the name of the server.
     *
     * @return Returns the name of the server.
     */
    public @NotNull String getName() {
        return data.name();
    }

    /**
     * Gets the data of the server.
     *
     * @return Returns the data of the server.
     */
    public @NotNull TransferableServerData getData() {
        return data;
    }

    /**
     * Gets the list of players currently connected to the server.
     *
     * @return Returns the list of players currently connected to the server.
     */
    public @NotNull Set<OCPlayer> getPlayers() {
        return players;
    }

    /**
     * Updates the data of the server. (Unsafe, not recommended to use)
     *
     * @param data The new data of the server
     */
    public void updateData(@NotNull TransferableServerData data) {
        this.data = data;
    }

    /** Represents the type of a server. */
    public enum Type {
        /** Represents a paper server. */
        PAPER(false, "stop", "platform/paper"),

        /** Represents a fabric server. */
        FABRIC(false, "stop", "platform/fabric"),

        /** Represents a forge server. */
        FORGE(false, "stop", "platform/forge"),

        /** Represents a velocity server. */
        VELOCITY(true, "shutdown", "platform/velocity");

        private final boolean proxy;
        private final @NotNull String stopCommand;
        private final @NotNull String template;

        Type(boolean proxy, @NotNull String stopCommand, @NotNull String template) {
            this.proxy = proxy;
            this.stopCommand = stopCommand;
            this.template = template;
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

        /**
         * Gets the template of the server.
         *
         * @return Returns the template of the server.
         */
        public @NotNull String getTemplate() {
            return template;
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

    public record TransferableServerData(
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
            int port) {
        @Override
        public String toString() {
            JsonObject json = new JsonObject();
            json.addProperty("task", task);
            json.addProperty("name", name);
            json.addProperty("type", type.name());
            json.addProperty("lifecycle_state", lifecycleState.name());
            json.addProperty("status", status.name());
            json.addProperty("auto_start", autoStart);
            json.addProperty("auto_delete", autoDelete);
            json.addProperty("executable", executable);
            json.addProperty("memory", memory);
            JsonArray jvmArgsArray = new JsonArray();
            jvmArgs.forEach(jvmArgsArray::add);
            json.add("jvmArgs", jvmArgsArray);
            JsonArray argsArray = new JsonArray();
            args.forEach(argsArray::add);
            json.add("args", argsArray);
            JsonObject environmentVariablesObject = new JsonObject();
            environmentVariables.forEach(environmentVariablesObject::addProperty);
            json.add("environment_variables", environmentVariablesObject);
            json.addProperty("port", port);
            return json.toString();
        }

        public static @NotNull TransferableServerData fromString(@NotNull String string) {
            JsonObject json = new JsonStreamParser(string).next().getAsJsonObject();
            String task = json.get("task").isJsonNull() ? null : json.get("task").getAsString();
            String name = json.get("name").getAsString();
            Type type = Type.valueOf(json.get("type").getAsString());
            LifecycleState lifecycleState =
                    LifecycleState.valueOf(json.get("lifecycle_state").getAsString());
            Status status = Status.valueOf(json.get("status").getAsString());
            boolean autoStart = json.get("auto_start").getAsBoolean();
            boolean autoDelete = json.get("auto_delete").getAsBoolean();
            String executable = json.get("executable").getAsString();
            int memory = json.get("memory").getAsInt();
            List<String> jvmArgs = new ArrayList<>();
            json.get("jvmArgs")
                    .getAsJsonArray()
                    .forEach(element -> jvmArgs.add(element.getAsString()));
            List<String> args = new ArrayList<>();
            json.get("args").getAsJsonArray().forEach(element -> args.add(element.getAsString()));
            Map<String, String> environmentVariables = new HashMap<>();
            json.get("environment_variables")
                    .getAsJsonObject()
                    .entrySet()
                    .forEach(
                            entry ->
                                    environmentVariables.put(
                                            entry.getKey(), entry.getValue().getAsString()));
            int port = json.get("port").getAsInt();
            return new TransferableServerData(
                    task,
                    name,
                    type,
                    lifecycleState,
                    status,
                    autoStart,
                    autoDelete,
                    executable,
                    memory,
                    jvmArgs,
                    args,
                    environmentVariables,
                    port);
        }
    }
}
