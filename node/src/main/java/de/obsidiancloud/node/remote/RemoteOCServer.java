package de.obsidiancloud.node.remote;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.OCServer;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RemoteOCServer extends OCServer {
    private final RemoteOCNode node;

    public RemoteOCServer(
            @Nullable String task,
            @NotNull String name,
            @NotNull OCServer.LifecycleState lifecycleState,
            @NotNull OCServer.Status status,
            @NotNull Type type,
            @NotNull String executable,
            int port,
            List<OCPlayer> players,
            int maxPlayers,
            boolean autoStart,
            boolean deleteOnStop,
            int memory,
            Map<String, String> environmentVariables,
            boolean maintenance,
            @NotNull RemoteOCNode node) {
        super(
                task,
                name,
                lifecycleState,
                status,
                type,
                executable,
                port,
                players,
                maxPlayers,
                autoStart,
                deleteOnStop,
                memory,
                environmentVariables,
                maintenance);
        this.node = node;
    }

    @Override
    public void start() {
        // TODO: Send start server packet to node
    }

    @Override
    public void stop() {
        // TODO: Send stop server packet to node
    }

    @Override
    public void kill() {
        // TODO: Send kill server packet to node
    }

    @Override
    public @NotNull OCNode getNode() {
        return node;
    }
}
