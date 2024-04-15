package de.obsidiancloud.node.threads;

import de.obsidiancloud.node.Node;
import de.obsidiancloud.node.local.LocalOCServer;
import java.util.logging.Level;
import org.springframework.util.FileSystemUtils;

public class ServerDeleteThread implements Runnable {
    private final LocalOCServer server;

    public ServerDeleteThread(LocalOCServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        Node.getInstance().getLogger().info("Deleting server " + server.getName() + "...");
        try {
            FileSystemUtils.deleteRecursively(server.getDirectory());
        } catch (Throwable exception) {
            Node.getInstance()
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "An error occurred while deleting server " + server.getName(),
                            exception);
        }
    }
}
