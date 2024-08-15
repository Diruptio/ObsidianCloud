package de.obsidiancloud.node.util;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.OCTask;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class TaskParser {
    /**
     * Parses a task from a file.
     *
     * @param file The file to parse the task from.
     * @return The parsed task or null if the file is invalid.
     * @throws IOException If an I/O error occurs.
     */
    public static @Nullable OCTask parse(Path file) throws IOException {
        String name = null;
        OCServer.Type type = null;
        boolean autoStart = false;
        boolean autoDelete = false;
        String executable = "java";
        int memory = 1024;
        List<String> jvmArgs = new ArrayList<>();
        List<String> args = new ArrayList<>();
        Map<String, String> environmentVariables = new HashMap<>();
        int port = 25565;
        List<String> templates = new ArrayList<>();
        int minAmount = 1;

        for (String line : Files.readAllLines(file)) {
            if (line.startsWith("#") || line.isEmpty()) continue;
            String[] parts = line.split(" ");

            String command = parts[0];
            if (command.equalsIgnoreCase("NAME") && parts.length == 2) {
                name = parts[1];
            }
            if (command.equalsIgnoreCase("TYPE") && parts.length == 2) {
                type = OCServer.Type.valueOf(parts[1]);
            }
            if (command.equalsIgnoreCase("AUTOSTART")) {
                autoStart = true;
            }
            if (command.equalsIgnoreCase("AUTODELETE")) {
                autoDelete = true;
            }
            if (command.equalsIgnoreCase("EXECUTABLE") && parts.length == 2) {
                executable = parts[1];
            }
            if (command.equalsIgnoreCase("MEMORY") && parts.length == 2) {
                memory = Integer.parseInt(parts[1]);
            }
            if (command.equalsIgnoreCase("JVM_ARGS") && parts.length >= 2) {
                for (int i = 1; i < parts.length; i++) {
                    if (parts[i].equals("%Flags%")) {
                        jvmArgs.addAll(List.of(Flags.AIKARS_FLAGS));
                    } else {
                        jvmArgs.add(parts[i]);
                    }
                }
            }
            if (command.equalsIgnoreCase("ARGS") && parts.length >= 2) {
                args.addAll(List.of(parts).subList(1, parts.length));
            }
            if (command.equalsIgnoreCase("ENV") && parts.length == 3) {
                environmentVariables.put(parts[1], parts[2]);
            }
            if (command.equalsIgnoreCase("PORT") && parts.length == 2) {
                port = Integer.parseInt(parts[1]);
            }
            if (command.equalsIgnoreCase("FROM") && parts.length == 2) {
                templates.add(parts[1]);
            }
            if (command.equalsIgnoreCase("AMOUNT") && parts.length == 2) {
                minAmount = Integer.parseInt(parts[1]);
            }
        }

        if (name == null || type == null || templates.isEmpty()) {
            return null;
        } else {
            return new OCTask(
                    name,
                    type,
                    autoStart,
                    autoDelete,
                    executable,
                    memory,
                    jvmArgs,
                    args,
                    environmentVariables,
                    port,
                    templates,
                    minAmount);
        }
    }
}
