package de.obsidiancloud.node.local.template;

import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

public abstract class OCTemplate {
    private final String path;

    public OCTemplate(@NotNull String path) {
        this.path = path;
    }

    public abstract void apply(@NotNull Path targetDirectory);

    public @NotNull String getPath() {
        return path;
    }
}
