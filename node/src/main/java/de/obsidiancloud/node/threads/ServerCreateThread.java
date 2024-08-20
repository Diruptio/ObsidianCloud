package de.obsidiancloud.node.threads;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.event.EventManager;
import de.obsidiancloud.common.event.ServerAddedEvent;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.packets.ServerAddedPacket;
import de.obsidiancloud.node.ObsidianCloudNode;
import de.obsidiancloud.node.local.LocalOCServer;
import de.obsidiancloud.node.local.template.OCTemplate;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.springframework.util.FileSystemUtils;

public class ServerCreateThread extends Thread {
    private final LocalOCServer server;
    private final List<String> templates;

    public ServerCreateThread(LocalOCServer server, List<String> templates) {
        super("ServerLoadThread-" + server.getName());
        this.server = server;
        this.templates = templates;
    }

    @Override
    public void run() {
        ObsidianCloudNode.getLogger().info("Creating server " + server.getName() + "...");
        try {
            Path directory = server.getDirectory();
            if (Files.exists(directory)) {
                FileSystemUtils.deleteRecursively(directory);
            }
            Files.createDirectories(directory);
            List<String> templates = new ArrayList<>(this.templates);
            templates.add(server.getData().type().getTemplate());
            for (String template : templates) {
                OCTemplate t = ObsidianCloudNode.getTemplate(template);
                if (t != null) t.apply(directory);
            }
            server.setLifecycleState(OCServer.LifecycleState.OFFLINE);
            server.setStatus(LocalOCServer.Status.OFFLINE);

            for (Connection connection : ObsidianCloudNode.getNetworkServer().getConnections()) {
                ServerAddedPacket packet = new ServerAddedPacket();
                packet.setNode(ObsidianCloudAPI.get().getLocalNode().getName());
                packet.setServerData(server.getData());
                connection.send(packet);
            }
            EventManager.call(new ServerAddedEvent(server));
        } catch (Throwable exception) {
            ObsidianCloudNode.getLogger()
                    .log(
                            Level.SEVERE,
                            "An error occurred while creating server " + server.getName(),
                            exception);
        }
    }
}
