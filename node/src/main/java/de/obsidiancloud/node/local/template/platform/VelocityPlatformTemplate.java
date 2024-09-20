package de.obsidiancloud.node.local.template.platform;

import de.obsidiancloud.node.NodeBuildConstants;
import de.obsidiancloud.node.ObsidianCloudNode;
import de.obsidiancloud.node.local.template.OCTemplate;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public class VelocityPlatformTemplate extends OCTemplate {
    private final Logger logger = ObsidianCloudNode.getLogger();

    public VelocityPlatformTemplate(@NotNull String path) {
        super(path);
    }

    @Override
    public void apply(@NotNull Path targetDirectory) {
        try {
            String name = NodeBuildConstants.VELOCITY_PLATFORM_FILE;
            Path pluginsDirectory = targetDirectory.resolve("plugins");
            if (!Files.exists(pluginsDirectory)) Files.createDirectories(pluginsDirectory);
            Path target = pluginsDirectory.resolve(name);
            InputStream in = getClass().getResourceAsStream("/" + name);
            OutputStream out = Files.newOutputStream(target);
            Objects.requireNonNull(in).transferTo(out);
            in.close();
        } catch (Throwable exception) {
            logger.log(Level.SEVERE, "Failed to apply template " + getPath(), exception);
        }
    }
}
