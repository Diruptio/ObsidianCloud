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
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

public class ServerCreateThread extends Thread {
    private final @NotNull LocalOCServer server;
    private final @NotNull List<String> templates;

    public ServerCreateThread(@NotNull LocalOCServer server, @NotNull List<String> templates) {
        super("ServerCreateThread-" + server.getName());
        this.server = server;
        this.templates = templates;
    }

    @Override
    public void run() {
        ObsidianCloudNode.getLogger().info("Creating server " + server.getName() + "...");
        for (Connection connection : ObsidianCloudNode.getNetworkServer().getConnections()) {
            ServerAddedPacket packet = new ServerAddedPacket();
            packet.setNode(ObsidianCloudAPI.get().getLocalNode().getName());
            packet.setServerData(server.getData());
            packet.setServerStatus(server.getStatus());
            connection.send(packet);
        }
        try {
            Path directory = server.getDirectory();
            if (Files.exists(directory)) {
                FileUtils.deleteDirectory(directory.toFile());
            }
            Files.createDirectories(directory);
            List<String> templates = new ArrayList<>(this.templates);
            OCServer.Platform platform = server.getData().platform();
            if (platform != null) {
                templates.addAll(platform.templates());
            }
            for (String template : templates) {
                OCTemplate t = ObsidianCloudNode.getTemplate(template);
                if (t != null) t.apply(directory);
            }
            server.setStatus(LocalOCServer.Status.OFFLINE);
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
