package de.obsidiancloud.node.local.template.simple;

import de.obsidiancloud.node.local.template.OCTemplate;
import de.obsidiancloud.node.local.template.TemplateProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

public class SimpleTemplateProvider implements TemplateProvider {
    private final Path templatesDirectory = Path.of("templates");

    @Override
    public OCTemplate getTemplate(@NotNull String name) {
        if (Files.exists(templatesDirectory.resolve(name))) {
            return new SimpleTemplate(name);
        } else {
            return null;
        }
    }
}
