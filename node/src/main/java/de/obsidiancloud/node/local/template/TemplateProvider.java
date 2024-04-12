package de.obsidiancloud.node.local.template;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TemplateProvider {
    public @Nullable OCTemplate getTemplate(@NotNull String name);
}
