package de.obsidiancloud.node.plugin;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

public interface PluginLoader {
    /** Loads all plugins */
    void loadPlugins();

    /** Unloads all plugins */
    void unloadPlugins();

    /**
     * Gets all loaded plugins
     *
     * @return All loaded plugins
     */
    @NotNull
    Map<PluginInfo, Plugin> getLoadedPlugins();
}
