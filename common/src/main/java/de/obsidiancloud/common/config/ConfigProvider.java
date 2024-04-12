package de.obsidiancloud.common.config;

import org.jetbrains.annotations.Nullable;

public interface ConfigProvider {
    /**
     * Gets the config.
     *
     * @return Returns the config if it has been loaded, otherwise {@code null}.
     */
    public @Nullable Config getConfig();

    /** Reloads the config. */
    public void reloadConfig();

    /** Saves the config. */
    public void saveConfig();
}
