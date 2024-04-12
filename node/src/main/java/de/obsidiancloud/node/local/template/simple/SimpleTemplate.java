package de.obsidiancloud.node.local.template.simple;

import de.obsidiancloud.node.Node;
import de.obsidiancloud.node.local.template.OCTemplate;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public class SimpleTemplate extends OCTemplate {
    private final Path templatesDirectory = Path.of("templates");
    private final Logger logger = Node.getInstance().getLogger();

    public SimpleTemplate(@NotNull String path) {
        super(path);
    }

    @Override
    public void apply(@NotNull Path targetDirectory) {
        try (Stream<Path> files = Files.list(templatesDirectory)) {
            for (Path file : files.toList()) {
                Files.copy(
                        file,
                        targetDirectory.resolve(file.getFileName()),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Throwable exception) {
            logger.log(Level.SEVERE, "Failed to apply template " + getPath(), exception);
        }
    }
}
