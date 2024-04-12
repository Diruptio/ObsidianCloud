package de.obsidiancloud.common.config;

import de.obsidiancloud.common.config.serializer.ConfigSerializer;
import de.obsidiancloud.common.config.serializer.JsonConfigSerializer;
import de.obsidiancloud.common.config.serializer.YamlConfigSerializer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

public class Config extends ConfigSection {
    private final Path file;
    private final Type type;

    /**
     * Creates a new config.
     *
     * @param file The file to load the config from.
     * @param type The type of the config.
     */
    public Config(@NotNull Path file, @NotNull Type type) {
        super(new HashMap<>());
        this.file = file;
        this.type = type;
        reload();
    }

    /** Saves the config to the file. */
    public void save() {
        try {
            if (Files.notExists(file.toAbsolutePath().getParent())) {
                Files.createDirectories(file.toAbsolutePath().getParent());
            }
            Files.writeString(file, type.serializer.serialize(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Reloads the config from the file. */
    public void reload() {
        try {
            if (Files.notExists(file.toAbsolutePath().getParent())) {
                Files.createDirectories(file.toAbsolutePath().getParent());
            }
            if (Files.exists(file)) {
                data = type.serializer.deserialize(Files.readString(file));
            } else {
                data = new HashMap<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the file of this config.
     *
     * @return Returns the file of this config.
     */
    public Path getFile() {
        return file;
    }

    public enum Type {
        JSON(new JsonConfigSerializer()),
        YAML(new YamlConfigSerializer());

        private final ConfigSerializer serializer;

        Type(ConfigSerializer serializer) {
            this.serializer = serializer;
        }
    }
}
