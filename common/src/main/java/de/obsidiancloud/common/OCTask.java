package de.obsidiancloud.common;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a task in the cluster.
 *
 * @param name The name of the task
 * @param type The type of the servers created by the task
 * @param staticServer Whether the servers should be deleted when they are stopped
 * @param autoStart Whether the servers should automatically start
 * @param executable The java executable of the servers
 * @param memory The amount of memory allocated to the servers
 * @param jvmArgs The JVM arguments of the servers
 * @param args The arguments of the servers
 * @param environmentVariables The environment variables of the servers
 * @param port The minimum port number on which the servers are running
 * @param templates The templates of the servers
 * @param minAmount The minimum amount of servers
 */
public record OCTask(
        @NotNull String name,
        @NotNull OCServer.Type type,
        @Nullable OCServer.Platform platform,
        boolean staticServer,
        boolean autoStart,
        String executable,
        int memory,
        @NotNull List<String> jvmArgs,
        @NotNull List<String> args,
        @NotNull Map<String, String> environmentVariables,
        int port,
        @NotNull List<String> templates,
        int minAmount) {}
