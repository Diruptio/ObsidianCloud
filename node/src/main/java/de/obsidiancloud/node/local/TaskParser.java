package de.obsidiancloud.node.local;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.OCTask;
import de.obsidiancloud.common.config.Config;
import de.obsidiancloud.common.config.ConfigSection;
import java.nio.file.Path;
import java.util.*;
import org.jetbrains.annotations.NotNull;

/** A parser for tasks. */
public class TaskParser {
    /**
     * Parses a task from a file.
     *
     * @param file The file to parse the task from.
     * @return The parsed task or null if the file is invalid.
     * @throws IllegalArgumentException If the file is not a valid task file.
     */
    public static @NotNull OCTask parse(@NotNull Path file) {
        Config.Type configType;
        if (file.toString().endsWith(".json")) {
            configType = Config.Type.JSON;
        } else if (file.toString().endsWith(".yml") || file.toString().endsWith(".yaml")) {
            configType = Config.Type.YAML;
        } else {
            throw new IllegalArgumentException("Invalid file type: " + file.getFileName());
        }
        Config config = new Config(file, configType);

        String name = config.getString("name");
        if (name == null) {
            throw new IllegalArgumentException(
                    "\"name\" (string) is missing in task file: " + file.getFileName());
        }

        String typeName = config.getString("type");
        if (typeName == null) {
            throw new IllegalArgumentException(
                    "\"type\" (string) is missing in task file: " + file.getFileName());
        }
        OCServer.Type type;
        try {
            type = OCServer.Type.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "\"type\" (string) is none of "
                            + Arrays.toString(OCServer.Type.values())
                            + " in task file: "
                            + file.getFileName());
        }

        String platformName = config.getString("platform");
        if (platformName == null) {
            throw new IllegalArgumentException(
                    "\"platform\" (string) is missing in task file: " + file.getFileName());
        }
        OCServer.Platform platform = null;
        if (type != OCServer.Type.CUSTOM) {
            for (OCServer.Platform p : OCServer.Platform.getPlatforms()) {
                if (p.name().equalsIgnoreCase(platformName)) {
                    platform = p;
                    break;
                }
            }
            if (platform == null) {
                throw new IllegalArgumentException(
                        "\"platform\" (string) is none of "
                                + OCServer.Platform.getPlatforms().stream()
                                        .map(OCServer.Platform::name)
                                        .toList()
                                + " in task file: "
                                + file.getFileName());
            }
        }

        boolean staticServer = false;
        if (config.contains("static")) staticServer = config.getBoolean("static");

        boolean autoStart = false;
        if (config.contains("auto_start")) autoStart = config.getBoolean("auto_start");

        String executable = "java";
        if (type == OCServer.Type.CUSTOM && !config.contains("executable")) {
            throw new IllegalArgumentException(
                    "\"executable\" (string) is missing in task file: " + file.getFileName());

        } else if (config.contains("executable")) {
            executable = config.getString("executable");
        }

        int memory;
        if (type == OCServer.Type.CUSTOM) {
            memory = 0;
        } else if (config.contains("memory")) {
            memory = config.getInt("memory");
        } else {
            throw new IllegalArgumentException(
                    "\"memory\" (int) is missing in task file: " + file.getFileName());
        }

        List<String> jvmArgs = config.getList("jvm_args", new ArrayList<>());

        List<String> args = config.getList("args", new ArrayList<>());

        Map<String, String> environmentVariables = new HashMap<>();
        ConfigSection env = config.getSection("env");
        if (env != null) {
            for (String key : env.getData().keySet()) {
                environmentVariables.put(key, env.get(key).toString());
            }
        }

        int port = 25565;
        if (config.contains("port")) {
            port = config.getInt("port");
        }

        List<String> templates;
        if (config.contains("templates")) {
            templates = config.getList("templates", new ArrayList<>());
        } else {
            throw new IllegalArgumentException(
                    "\"templates\" (list) is missing in task file: " + file.getFileName());
        }

        int minAmount;
        if (config.contains("min_amount")) {
            minAmount = config.getInt("min_amount");
        } else {
            throw new IllegalArgumentException(
                    "\"min_amount\" (int) is missing in task file: " + file.getFileName());
        }

        return new OCTask(
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
                templates,
                minAmount);
    }
}
