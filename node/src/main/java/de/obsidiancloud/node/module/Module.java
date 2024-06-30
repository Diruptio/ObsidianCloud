package de.obsidiancloud.node.module;

import de.obsidiancloud.common.command.Command;
import de.obsidiancloud.common.command.CommandProvider;
import de.obsidiancloud.node.config.Config;
import de.obsidiancloud.node.config.ConfigProvider;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

/** A module for the node */
public class Module implements CommandProvider, ConfigProvider {
    private final List<Command> commands = new ArrayList<>();
    private ModuleManifest manifest;
    private Config config = null;

    public void onEnable() {}

    public void onDisable() {}

    @Override
    public void registerCommand(@NotNull Command command) {
        if (commands.contains(command)) {
            throw new IllegalArgumentException(
                    "Command \"" + command.getName() + "\" is already registered");
        } else {
            commands.add(command);
        }
    }

    @Override
    public void unregisterCommand(@NotNull String name) {
        Command command = getCommand(name);
        if (command == null) {
            throw new IllegalArgumentException("Command \"" + name + "\" is not registered");
        } else {
            commands.remove(command);
        }
    }

    @Override
    public @NotNull List<Command> getCommands() {
        return commands;
    }

    @Override
    public Command getCommand(@NotNull String name) {
        for (Command command : commands) {
            if (command.getName().equalsIgnoreCase(name)) {
                return command;
            }
        }
        return null;
    }

    public @NotNull Logger getLogger() {
        return Logger.getLogger(manifest.getName());
    }

    /**
     * Gets the manifest of the addon
     *
     * @return The manifest of the addon
     */
    @SuppressWarnings("unused")
    public ModuleManifest getManifest() {
        return manifest;
    }

    /**
     * Gets the data folder of the addon
     *
     * @return The data folder of the addon
     */
    public Path getDataFolder() {
        return manifest == null ? null : manifest.getFile().getParent().resolve(manifest.getName());
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public void reloadConfig() {
        if (config == null) {
            config = new Config(getDataFolder().resolve("config.yml"), Config.Type.YAML);
        } else {
            config.reload();
        }
    }

    @Override
    public void saveConfig() {
        config.save();
    }
}
