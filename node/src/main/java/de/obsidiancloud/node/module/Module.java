package de.obsidiancloud.node.module;

import de.obsidiancloud.common.command.Command;
import de.obsidiancloud.common.command.CommandProvider;
import de.obsidiancloud.node.config.Config;
import de.obsidiancloud.node.config.ConfigProvider;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * A module for the node
 */
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

    /**
     * Gets the manifest of the addon
     *
     * @return The manifest of the addon
     */
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

    public static class ModuleClassLoader extends URLClassLoader {
        public ModuleClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            return internalLoadClass(name, resolve, true);
        }

        private Class<?> internalLoadClass(String name, boolean resolve, boolean checkOther)
                throws ClassNotFoundException {
            try {
                return super.loadClass(name, resolve);
            } catch (ClassNotFoundException exception) {
                if (checkOther) {
                    for (Module module : ModuleLoader.getModules()) {
                        try {
                            if (module.classLoader() != this) {
                                return module.classLoader().internalLoadClass(name, resolve, false);
                            }
                        } catch (Throwable ignored) {
                        }
                    }
                }
                throw exception;
            }
        }
    }
}
