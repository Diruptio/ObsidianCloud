package de.obsidiancloud.node.local.template;

import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

public abstract class OCTemplate {
    protected static final int startPort = 40000;
    private final @NotNull String path;

    /**
     * Creates a new template.
     *
     * @param path The path of the template
     */
    public OCTemplate(@NotNull String path) {
        this.path = path;
    }

    /**
     * Applies the template to the target directory.
     *
     * @param targetDirectory The target directory
     */
    public abstract void apply(@NotNull Path targetDirectory);

    /**
     * Gets the path of the template.
     *
     * @return The path of the template
     */
    public @NotNull String getPath() {
        return path;
    }
}
