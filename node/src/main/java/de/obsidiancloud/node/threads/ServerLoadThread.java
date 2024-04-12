package de.obsidiancloud.node.threads;

import de.obsidiancloud.node.Node;
import de.obsidiancloud.node.local.LocalOCServer;
import de.obsidiancloud.node.local.template.OCTemplate;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.util.FileSystemUtils;

public class ServerLoadThread extends Thread {
    private final LocalOCServer server;
    private final List<String> templates;

    public ServerLoadThread(LocalOCServer server, List<String> templates) {
        super("ServerLoadThread-" + server.getName());
        this.server = server;
        this.templates = templates;
    }

    @Override
    public void run() {
        Node.getInstance().getLogger().info("Loading server " + server.getName() + "...");
        try {
            Path directory = server.getDirectory();
            if (Files.exists(directory)) {
                FileSystemUtils.deleteRecursively(directory);
            }
            Files.createDirectories(directory);
            for (String template : templates) {
                OCTemplate t = Node.getInstance().getTemplate(template);
                if (t != null) {
                    t.apply(directory);
                }
            }
            server.setStatus(LocalOCServer.Status.OFFLINE);
        } catch (Throwable exception) {
            exception.printStackTrace(System.err);
        }
    }
}
