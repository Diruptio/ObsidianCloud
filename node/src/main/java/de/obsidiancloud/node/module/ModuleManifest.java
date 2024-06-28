package de.obsidiancloud.node.module;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;


public class ModuleManifest {
    private final @NotNull Path file;
    private final @NotNull String name;
    private final @NotNull String mainClass;
    private final @NotNull String version;
    private final @NotNull List<String> authors;
    private final @Nullable String description;
    private final @NotNull List<String> dependencies;
    private final @NotNull List<String> softDependencies;
    private ModuleLoader.AddonClassLoader classLoader = null;
    private Module instance = null;

    /**
     * Create a new addon manifest
     *
     * @param file The file of the module
     * @param name The name of the module
     * @param mainClass The main class of the module
     * @param version The version of the module
     * @param authors The authors of the module
     * @param description The description of the module
     * @param dependencies The dependencies of the module
     * @param softDependencies The soft dependencies of the module
     */
    public ModuleManifest(
            @NotNull Path file,
            @NotNull String name,
            @NotNull String mainClass,
            @NotNull String version,
            @NotNull List<String> authors,
            @Nullable String description,
            @NotNull List<String> dependencies,
            @NotNull List<String> softDependencies) {
        this.file = file;
        this.name = name;
        this.mainClass = mainClass;
        this.version = version;
        this.authors = authors;
        this.description = description;
        this.dependencies = dependencies;
        this.softDependencies = softDependencies;
    }

    /**
     * Gets the file of the module
     *
     * @return The file of the module
     */
    public @NotNull Path getFile() {
        return file;
    }

    /**
     * Gets the name of the module
     *
     * @return The name of the module
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Gets the main class of the module
     *
     * @return The main class of the module
     */
    public @NotNull String getMainClass() {
        return mainClass;
    }

    /**
     * Gets the version of the module
     *
     * @return The version of the module
     */
    public @NotNull String getVersion() {
        return version;
    }

    /**
     * Gets the authors of the module
     *
     * @return The authors of the module
     */
    public @NotNull List<String> getAuthors() {
        return authors;
    }

    /**
     * Gets the description of the module
     *
     * @return The description of the module
     */
    public @Nullable String getDescription() {
        return description;
    }

    /**
     * Gets the dependencies of the module
     *
     * @return The dependencies of the module
     */
    public @NotNull List<String> getDependencies() {
        return dependencies;
    }

    /**
     * Gets the soft dependencies of the module
     *
     * @return The soft dependencies of the module
     */
    public @NotNull List<String> getSoftDependencies() {
        return softDependencies;
    }

    /**
     * Gets the class loader of the module
     *
     * @return The class loader of the module
     */
    public ModuleLoader.AddonClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Check if the addon is enabled
     *
     * @return Weather the addon is enabled
     */
    public boolean isEnabled() {
        return instance != null;
    }

    /**
     * Gets the instance of the module
     *
     * @return The instance of the module
     */
    public Module getInstance() {
        return instance;
    }

    /**
     * Loads the module manifest from a jar file
     *
     * @param file The file to load
     * @throws IOException If an error occurs while loading the manifest
     * @return The module manifest
     */
    public static @NotNull ModuleManifest loadManifest(@NotNull Path file) throws IOException {
        try (JarFile jarFile = new JarFile(file.toFile())) {
            JarEntry addonYml = jarFile.getJarEntry("module.yml");
            if (addonYml == null) {
                throw new IOException("Could not find module.yml in \"" + file.getFileName() + "\"");
            }
            Map<String, Object> yml = new Yaml().load(jarFile.getInputStream(addonYml));
            jarFile.close();
            if (yml == null) {
                throw new IOException("Could parse module.yml in \"" + file.getFileName() + "\"");
            }
            if (!yml.containsKey("name") || !(yml.get("name") instanceof String name)) {
                throw new IOException(
                        "Could not find \"name\" in module.yml in \"" + file.getFileName() + "\"");
            }
            if (!yml.containsKey("main") || !(yml.get("main") instanceof String main)) {
                throw new IOException(
                        "Could not find \"main\" in module.yml in \"" + file.getFileName() + "\"");
            }
            if (!yml.containsKey("version") || !(yml.get("version") instanceof String version)) {
                throw new IOException(
                        "Could not find \"version\" in module.yml in \""
                                + file.getFileName()
                                + "\"");
            }
            List<String> authors = new ArrayList<>();
            if (yml.containsKey("authors") && yml.get("authors") instanceof String[] array) {
                authors.addAll(Arrays.asList(array));
            }
            String description = null;
            if (yml.containsKey("description") && yml.get("description") instanceof String desc) {
                description = desc;
            }
            List<String> dependencies = new ArrayList<>();
            if (yml.containsKey("depend")
                    && yml.get("depend") instanceof String[] array) {
                dependencies.addAll(Arrays.asList(array));
            }
            List<String> softDependencies = new ArrayList<>();
            if (yml.containsKey("softdepend")
                    && yml.get("softdepend") instanceof String[] array) {
                dependencies.addAll(Arrays.asList(array));
            }
            return new ModuleManifest(
                    file,
                    name,
                    main,
                    version,
                    authors,
                    description,
                    dependencies,
                    softDependencies);
        } catch (IOException exception) {
            throw new IOException(
                    "Could not load manifest of \"" + file.getFileName() + "\"", exception);
        }
    }
}
