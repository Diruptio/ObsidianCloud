package de.obsidiancloud.common;

import java.util.List;
import java.util.Map;

public record OCTask(
        String name,
        OCServer.Type type,
        int port,
        int maxPlayers,
        boolean autoStart,
        boolean autoDelete,
        int memory,
        Map<String, String> environmentVariables,
        List<String> templates,
        int minAmount) {}
