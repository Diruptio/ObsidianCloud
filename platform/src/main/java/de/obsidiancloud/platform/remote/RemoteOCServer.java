package de.obsidiancloud.platform.remote;

import de.obsidiancloud.common.OCServer;
import java.util.ArrayList;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RemoteOCServer extends OCServer {
    private final @NotNull RemoteOCNode node;

    public RemoteOCServer(
            @Nullable String task,
            @NotNull String name,
            @NotNull Type type,
            @NotNull LifecycleState lifecycleState,
            @NotNull Status status,
            boolean autoStart,
            int memory,
            int port,
            @NotNull RemoteOCNode node) {
        super(
                task,
                name,
                type,
                lifecycleState,
                status,
                autoStart,
                "",
                memory,
                new ArrayList<>(),
                new ArrayList<>(),
                new HashMap<>(),
                port,
                new ArrayList<>());
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
    public @NotNull RemoteOCNode getNode() {
        return node;
    }
}
