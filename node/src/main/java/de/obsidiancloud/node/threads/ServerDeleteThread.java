package de.obsidiancloud.node.threads;

import de.obsidiancloud.node.ObsidianCloudNode;
import de.obsidiancloud.node.local.LocalOCServer;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

public class ServerDeleteThread implements Runnable {
    private final @NotNull LocalOCServer server;

    public ServerDeleteThread(@NotNull LocalOCServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        ObsidianCloudNode.getLogger().info("Deleting server " + server.getName() + "...");
        try {
            FileUtils.deleteDirectory(server.getDirectory().toFile());
        } catch (Throwable exception) {
            ObsidianCloudNode.getLogger()
                    .log(
                            Level.SEVERE,
                            "An error occurred while deleting server " + server.getName(),
                            exception);
        }
    }
}
