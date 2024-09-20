package de.obsidiancloud.common.config;

import org.jetbrains.annotations.Nullable;

public interface ConfigProvider {
    /**
     * Gets the config.
     *
     * @return Returns the config if it has been loaded, otherwise {@code null}.
     */
    @Nullable
    Config getConfig();

    /** Reloads the config. */
    void reloadConfig();

    /** Saves the config. */
    void saveConfig();
}
