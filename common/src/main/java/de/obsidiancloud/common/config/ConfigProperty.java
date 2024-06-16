package de.obsidiancloud.common.config;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigProperty<T> {
    private final @NotNull ConfigSection section;
    private final @NotNull String key;
    private final @Nullable T defaultValue;

    public ConfigProperty(
            @NotNull ConfigSection section, @NotNull String key, @Nullable T defaultValue) {
        this.section = section;
        this.key = key;
        this.defaultValue = defaultValue;
        section.setDefault(key, defaultValue);
    }

    public ConfigProperty(@NotNull ConfigSection section, @NotNull String key) {
        this(section, key, null);
    }

    @SuppressWarnings("unchecked")
    public @NotNull T get() {
        return (T) Objects.requireNonNull(section.get(key));
    }
}
