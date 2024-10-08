package de.obsidiancloud.common;

import com.google.gson.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represents a server in the cluster. */
public abstract class OCServer {
    /** The data of the server. */
    protected @NotNull TransferableServerData data;

    /** The status of the server. */
    protected @NotNull Status status;

    /** The port of the server. */
    protected int port = -1;

    private final @NotNull Set<OCPlayer> players;

    /**
     * Create a new OCServer with the specified parameters.
     *
     * @param data The data of the server
     * @param status The status of the server
     * @param players The list of players currently connected to the server
     */
    public OCServer(@NotNull TransferableServerData data, @NotNull Status status, @NotNull Set<OCPlayer> players) {
        this.data = data;
        this.status = status;
        this.players = players;
    }

    /** Starts the server. */
    public abstract void start();

    /** Stops the server. */
    public abstract void stop();

    /** Kills the server. */
    public abstract void kill();

    /**
     * Sets the name of the server.
     *
     * @param name The name
     */
    public abstract void setName(@NotNull String name);

    /**
     * Sets whether the server should start automatically.
     *
     * @param autoStart Whether the server should start automatically
     */
    public abstract void setAutoStart(boolean autoStart);

    /**
     * Sets the executable of the server.
     *
     * @param executable The executable
     */
    public abstract void setExecutable(@NotNull String executable);

    /**
     * Sets the memory of the server.
     *
     * @param memory The memory
     */
    public abstract void setMemory(int memory);

    /**
     * Sets the JVM arguments of the server.
     *
     * @param jvmArgs The JVM arguments
     */
    public abstract void setJvmArgs(@NotNull List<String> jvmArgs);

    /**
     * Sets the arguments of the server.
     *
     * @param args The arguments
     */
    public abstract void setArgs(@NotNull List<String> args);

    /**
     * Sets the environment variables of the server.
     *
     * @param environmentVariables The environment variables
     */
    public abstract void setEnvironmentVariables(@NotNull Map<String, String> environmentVariables);

    /**
     * Sets the proxies the server should be linked to.
     *
     * @param linkToProxies The proxies the server should be linked to
     */
    public abstract void setLinkToProxies(@Nullable List<String> linkToProxies);

    /**
     * Sets the server to be a fallback server.
     *
     * @param fallback Whether the server is a fallback server
     */
    public abstract void setFallback(boolean fallback);

    /**
     * Sets the minimum port of the server.
     *
     * @param port The minimum port
     */
    public abstract void setPort(int port);

    /**
     * Gets the status of the server.
     *
     * @return The status
     */
    public @NotNull Status getStatus() {
        return status;
    }

    /**
     * Sets the status of the server.
     *
     * @param status The status
     */
    public abstract void setStatus(@NotNull Status status);

    /**
     * Gets the port of the server.
     *
     * @return The port
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the node of the server.
     *
     * @return The node
     */
    public abstract @NotNull OCNode getNode();

    /**
     * Gets the name of the server. (This is equivalent to {@code getData().name()})
     *
     * @return The name
     */
    public @NotNull String getName() {
        return data.name();
    }

    /**
     * Gets the data of the server.
     *
     * @return The data
     */
    public @NotNull TransferableServerData getData() {
        return data;
    }

    /**
     * Gets the list of players currently connected to the server.
     *
     * @return The list of players
     */
    public @NotNull Set<OCPlayer> getPlayers() {
        return players;
    }

    /**
     * Updates the data of the server. (Unsafe, please do not use)
     *
     * @param data The new data
     */
    @ApiStatus.Internal
    public void updateData(@NotNull TransferableServerData data) {
        this.data = data;
    }

    /**
     * Updates the status of the server. (Unsafe, please do not use)
     *
     * @param status The new status
     */
    @ApiStatus.Internal
    public void updateStatus(@NotNull Status status) {
        this.status = status;
    }

    /**
     * Updates the port of the server. (Unsafe, please do not use)
     *
     * @param port The new port
     */
    @ApiStatus.Internal
    public void updatePort(int port) {
        this.port = port;
    }

    /** The types of servers. */
    public enum Type {
        /** A normal minecraft server. (Paper, Fabric, Forge) */
        SERVER,

        /** A minecraft proxy server. (Velocity) */
        PROXY,

        /**
         * Everything that is <b>not supported</b> by ObsidianCloud <b>can be executed</b> with the custom server type.
         * Fields platform, memory, jvmArgs and args will be <b>ignored</b> by ObsidianCloud.
         */
        CUSTOM
    }

    /**
     * The platform/software of a (non-custom) server.
     *
     * @param name The name of the platform
     * @param type The server type of the platform
     * @param stopCommand The terminal command to stop the server
     * @param templates The list of templates to apply for the server
     */
    public record Platform(
            @NotNull String name, @NotNull Type type, @NotNull String stopCommand, @NotNull List<String> templates) {
        private static final List<Platform> platforms = new ArrayList<>();

        /** A paper server. */
        public static final Platform PAPER = new Platform("paper", Type.SERVER, "stop", List.of("platform/paper"));

        /** A fabric server. */
        public static final Platform FABRIC = new Platform("fabric", Type.SERVER, "stop", List.of("platform/fabric"));

        /** A velocity server. */
        public static final Platform VELOCITY =
                new Platform("velocity", Type.PROXY, "shutdown", List.of("platform/velocity"));

        /**
         * Gets a list of all platforms.
         *
         * @return All platforms
         */
        public static @NotNull List<Platform> getPlatforms() {
            return platforms;
        }

        /**
         * Gets a platform by its name.
         *
         * @param name The name of the platform
         * @return The platform
         */
        public static @Nullable Platform getPlatform(@NotNull String name) {
            for (Platform platform : platforms) {
                if (platform.name().equalsIgnoreCase(name)) {
                    return platform;
                }
            }
            return null;
        }
    }

    /** Status of the server. */
    public enum Status {
        /** The server is being created. */
        CREATING,

        /** The server is offline. */
        OFFLINE,

        /** The server is starting. */
        STARTING,

        /** The server is online and ready. */
        READY,

        /** The server is online but not ready. */
        NOT_READY
    }

    /**
     * Represents the data of a server.
     *
     * @param task The task which created the server ({@code null} if not created by a task)
     * @param name The name of the server
     * @param type The type of the server
     * @param platform The platform of the server ({@code null} if type is {@link Type#CUSTOM})
     * @param staticServer If the server is static
     * @param autoStart If the server should be started automatically
     * @param executable The execution string of the server. This is equivalent to a <b>shell script</b> line. The
     *     placeholders <i>%SERVER_PORT%, %AIKARS_FLAGS%</i> will be replaced. If the type is {@link Type#SERVER} or
     *     {@link Type#PROXY}, this should be a java executable.
     * @param memory The memory of the server (will be ignored if type is {@link Type#CUSTOM})
     * @param jvmArgs The JVM arguments of the server (will be ignored if type is {@link Type#CUSTOM})
     * @param args The arguments of the server (will be ignored if type is {@link Type#CUSTOM})
     * @param environmentVariables The environment variables of the server
     * @param port The port of the server ({@code 0} if type is {@link Type#CUSTOM})
     * @param linkToProxies The list of proxies the server should be linked to or {@code null} if the server should not
     *     be linked to any proxies (will be ignored if type is {@link Type#CUSTOM})
     * @param fallback If the server is a fallback server (will be ignored if type is {@link Type#CUSTOM})
     */
    public record TransferableServerData(
            @Nullable String task,
            @NotNull String name,
            @NotNull Type type,
            @Nullable Platform platform,
            boolean staticServer,
            boolean autoStart,
            @NotNull String executable,
            int memory,
            @NotNull List<String> jvmArgs,
            @NotNull List<String> args,
            @NotNull Map<String, String> environmentVariables,
            int port,
            @Nullable List<String> linkToProxies,
            boolean fallback) {
        @Override
        public String toString() {
            JsonObject json = new JsonObject();
            json.addProperty("task", task);
            json.addProperty("name", name);
            json.addProperty("type", type.name());
            if (platform != null) {
                json.addProperty("platform", platform.name());
            }
            json.addProperty("static", staticServer);
            json.addProperty("auto_start", autoStart);
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
            if (linkToProxies != null) {
                JsonArray linkToProxiesArray = new JsonArray();
                linkToProxies.forEach(linkToProxiesArray::add);
                json.add("linkToProxies", linkToProxiesArray);
            }
            json.addProperty("fallback", fallback);
            return json.toString();
        }

        /**
         * Converts a {@link String} to a {@link TransferableServerData}.
         *
         * @param string The string
         * @return The server data
         */
        public static @NotNull TransferableServerData fromString(@NotNull String string) {
            JsonObject json = new JsonStreamParser(string).next().getAsJsonObject();
            String task =
                    json.get("task").isJsonNull() ? null : json.get("task").getAsString();
            String name = json.get("name").getAsString();
            Type type = Type.valueOf(json.get("type").getAsString());
            Platform platform = null;
            if (json.has("platform")) {
                for (Platform p : Platform.getPlatforms()) {
                    if (p.name().equalsIgnoreCase(json.get("platform").getAsString())) {
                        platform = p;
                        break;
                    }
                }
            }
            boolean staticServer = json.get("static").getAsBoolean();
            boolean autoStart = json.get("auto_start").getAsBoolean();
            String executable = json.get("executable").getAsString();
            int memory = json.get("memory").getAsInt();
            List<String> jvmArgs = new ArrayList<>();
            json.get("jvmArgs").getAsJsonArray().forEach(element -> jvmArgs.add(element.getAsString()));
            List<String> args = new ArrayList<>();
            json.get("args").getAsJsonArray().forEach(element -> args.add(element.getAsString()));
            Map<String, String> environmentVariables = new HashMap<>();
            json.get("environment_variables")
                    .getAsJsonObject()
                    .entrySet()
                    .forEach(entry -> environmentVariables.put(
                            entry.getKey(), entry.getValue().getAsString()));
            int port = json.get("port").getAsInt();
            List<String> linkToProxies = null;
            if (json.has("linkToProxies")) {
                linkToProxies = new ArrayList<>();
                for (JsonElement element : json.get("linkToProxies").getAsJsonArray()) {
                    linkToProxies.add(element.getAsString());
                }
            }
            boolean fallback = json.get("fallback").getAsBoolean();
            return new TransferableServerData(
                    task,
                    name,
                    type,
                    platform,
                    staticServer,
                    autoStart,
                    executable,
                    memory,
                    jvmArgs,
                    args,
                    environmentVariables,
                    port,
                    linkToProxies,
                    fallback);
        }
    }
}
