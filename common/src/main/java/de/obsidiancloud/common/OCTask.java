package de.obsidiancloud.common;

import java.util.List;
import java.util.Map;

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
