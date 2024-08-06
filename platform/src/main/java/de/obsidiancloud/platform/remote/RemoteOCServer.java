package de.obsidiancloud.platform.remote;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RemoteOCServer extends OCServer {
    private final @NotNull OCNode node;

    public RemoteOCServer(
            @Nullable String task,
            @NotNull String name,
            @NotNull Type type,
            @NotNull LifecycleState lifecycleState,
            @NotNull Status status,
            boolean autoStart,
            @NotNull OCNode node) {
        super(
                task,
                name,
                type,
                lifecycleState,
                status,
                autoStart,
                "",
                (int) Runtime.getRuntime().maxMemory(),
                new ArrayList<>(),
                new ArrayList<>(),
                new HashMap<>(),
                -1,
                new HashSet<>());
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
