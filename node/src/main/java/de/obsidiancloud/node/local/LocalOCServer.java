package de.obsidiancloud.node.local;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.node.Node;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocalOCServer extends OCServer {
    public LocalOCServer(
            @Nullable String task,
            @NotNull String name,
            @NotNull Status status,
            @NotNull Type type,
            int port,
            int maxPlayers,
            boolean autoStart,
            boolean autoDelete,
            int memory,
            Map<String, String> environmentVariables,
            boolean maintenance) {
        super(
                task,
                name,
                status,
                type,
                port,
                new ArrayList<>(),
                maxPlayers,
                autoStart,
                autoDelete,
                memory,
                environmentVariables,
                maintenance);
    }

    @Override
    public void start() {
        setStatus(Status.ONLINE);
        Node.getInstance().getLogger().info("Starting server " + getName() + "...");
        // TODO: Find unused port
        // TODO: Start server using de.obsidiancloud.node.util.AikarsFlags
    }

    @Override
    public void stop() {
        // TODO: Stop server
    }

    @Override
    public @NotNull OCNode getNode() {
        return Node.getInstance().getLocalNode();
    }

    public @NotNull Path getDirectory() {
        return Path.of("servers").resolve(getName());
    }
}
