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
    public static @Nullable OCTask parse(Path file) throws IOException {
        String name = null;
        OCServer.Type type = null;
        int port = 25565;
        int maxPlayers = -1;
        boolean autoStart = false;
        boolean autoDelete = false;
        int memory = 1024;
        Map<String, String> environmentVariables = new HashMap<>();
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
            if (command.equalsIgnoreCase("PORT") && parts.length == 2) {
                port = Integer.parseInt(parts[1]);
            }
            if (command.equalsIgnoreCase("AUTOSTART")) {
                autoStart = true;
            }
            if (command.equalsIgnoreCase("AUTODELETE")) {
                autoDelete = true;
            }
            if (command.equalsIgnoreCase("MEMORY") && parts.length == 2) {
                memory = Integer.parseInt(parts[1]);
            }
            if (command.equalsIgnoreCase("MAX_PLAYERS") && parts.length == 2) {
                maxPlayers = Integer.parseInt(parts[1]);
            }
            if (command.equalsIgnoreCase("ENV") && parts.length == 3) {
                environmentVariables.put(parts[1], parts[2]);
            }
            if (command.equalsIgnoreCase("FROM") && parts.length == 2) {
                templates.add(parts[1]);
            }
            if (command.equalsIgnoreCase("AMOUNT") && parts.length == 2) {
                minAmount = Integer.parseInt(parts[1]);
            }
        }

        if (name == null || templates.isEmpty()) {
            return null;
        } else {
            return new OCTask(
                    name,
                    type,
                    port,
                    maxPlayers,
                    autoStart,
                    autoDelete,
                    memory,
                    environmentVariables,
                    templates,
                    minAmount);
        }
    }
}
