package de.obsidiancloud.common;

import java.util.List;
import java.util.Map;

/**
 * Represents a task in the cluster.
 *
 * @param name The name of the task
 * @param type The type of the servers created by the task
 * @param autoStart Whether the servers should automatically start
 * @param autoDelete Whether the servers should be deleted when they are stopped
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
        String name,
        OCServer.Type type,
        boolean autoStart,
        boolean autoDelete,
        String executable,
        int memory,
        List<String> jvmArgs,
        List<String> args,
        Map<String, String> environmentVariables,
        int port,
        List<String> templates,
        int minAmount) {}
