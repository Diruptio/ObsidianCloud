package de.obsidiancloud.node.local.template.simple;

import de.obsidiancloud.node.ObsidianCloudNode;
import de.obsidiancloud.node.local.template.OCTemplate;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.FileSystemUtils;

public class SimpleTemplate extends OCTemplate {
    private final Path templatesDirectory = Path.of("templates");
    private final Logger logger = ObsidianCloudNode.getLogger();

    public SimpleTemplate(@NotNull String path) {
        super(path);
    }

    @Override
    public void apply(@NotNull Path targetDirectory) {
        try {
            FileSystemUtils.copyRecursively(templatesDirectory.resolve(getPath()), targetDirectory);
        } catch (Throwable exception) {
            logger.log(Level.SEVERE, "Failed to apply template " + getPath(), exception);
        }
    }
}
