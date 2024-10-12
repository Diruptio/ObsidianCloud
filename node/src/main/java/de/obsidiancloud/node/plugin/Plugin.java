package de.obsidiancloud.node.plugin;

import de.obsidiancloud.common.command.Command;
import de.obsidiancloud.common.command.CommandProvider;
import de.obsidiancloud.common.config.Config;
import de.obsidiancloud.common.config.ConfigProvider;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public class Plugin implements CommandProvider, ConfigProvider {
    private Logger logger = null;
    private final List<Command> commands = new ArrayList<>();
    PluginLoader loader = null;
    DefaultPluginLoader.PluginClassLoader classLoader = null;
    PluginInfo info = null;
    Config config = null;

    public void onLoad() {}

    public void onEnable() {}

    public void onDisable() {}

    @Override
    public void registerCommand(@NotNull Command command) {
        if (commands.contains(command)) {
            throw new IllegalArgumentException("Command \"" + command.getName() + "\" is already registered");
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

    @Override
    public @NotNull Config getConfig() {
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

    /**
     * Gets the data folder of the addon
     *
     * @return The data folder of the addon
     */
    public @NotNull Path getDataFolder() {
        return Path.of("plugins").resolve(info.name());
    }

    /**
     * Gets the logger of the plugin
     *
     * @return The logger
     */
    public @NotNull Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(info.name());
        }
        return logger;
    }

    /**
     * Gets the loader of the plugin
     *
     * @return The plugin loader
     */
    public @NotNull PluginLoader getLoader() {
        return loader;
    }

    /**
     * Gets the class loader of the plugin
     *
     * @return The class loader
     */
    public @NotNull DefaultPluginLoader.PluginClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Gets the info of the plugin
     *
     * @return The plugin info
     */
    public @NotNull PluginInfo getInfo() {
        return info;
    }
}
