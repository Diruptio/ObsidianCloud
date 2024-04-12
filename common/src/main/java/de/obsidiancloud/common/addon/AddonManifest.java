package de.obsidiancloud.common.addon;

import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AddonManifest {
    private final Path file;
    private final String name;
    private final String main;
    private final String version;
    private final List<String> authors;
    private final String description;
    private final List<String> dependencies;
    private final List<String> softDependencies;
    private AddonLoader.AddonClassLoader classLoader;
    private Addon instance;

    /**
     * Creates a new addon manifest
     *
     * @param file The file of the addon
     * @param name The name of the addon
     * @param main The main class of the addon
     * @param version The version of the addon
     * @param authors The authors of the addon
     * @param description The description of the addon
     * @param dependencies The dependencies of the addon
     * @param softDependencies The soft dependencies of the addon
     */
    public AddonManifest(
            @NotNull Path file,
            @NotNull String name,
            @NotNull String main,
            @NotNull String version,
            @NotNull List<String> authors,
            @Nullable String description,
            @NotNull List<String> dependencies,
            @NotNull List<String> softDependencies) {
        this.file = file;
        this.name = name;
        this.main = main;
        this.version = version;
        this.authors = authors;
        this.description = description;
        this.dependencies = dependencies;
        this.softDependencies = softDependencies;
    }

    /**
     * Gets the file of the addon
     *
     * @return The file of the addon
     */
    public Path getFile() {
        return file;
    }

    /**
     * Gets the name of the addon
     *
     * @return The name of the addon
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Gets the main class of the addon
     *
     * @return The main class of the addon
     */
    public @NotNull String getMain() {
        return main;
    }

    /**
     * Gets the version of the addon
     *
     * @return The version of the addon
     */
    public @NotNull String getVersion() {
        return version;
    }

    /**
     * Gets the authors of the addon
     *
     * @return The authors of the addon
     */
    public @NotNull List<String> getAuthors() {
        return authors;
    }

    /**
     * Gets the description of the addon
     *
     * @return The description of the addon
     */
    public @Nullable String getDescription() {
        return description;
    }

    /**
     * Gets the dependencies of the addon
     *
     * @return The dependencies of the addon
     */
    public @NotNull List<String> getDependencies() {
        return dependencies;
    }

    /**
     * Gets the soft dependencies of the addon
     *
     * @return The soft dependencies of the addon
     */
    public @NotNull List<String> getSoftDependencies() {
        return softDependencies;
    }

    /**
     * Gets the class loader of the addon
     *
     * @return The class loader of the addon
     */
    public AddonLoader.AddonClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Gets if the addon is enabled
     *
     * @return If the addon is enabled
     */
    public boolean isEnabled() {
        return instance != null;
    }

    /**
     * Gets the instance of the addon
     *
     * @return The instance of the addon
     */
    public Addon getInstance() {
        return instance;
    }
}
