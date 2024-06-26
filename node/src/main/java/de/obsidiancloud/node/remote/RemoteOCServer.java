package de.obsidiancloud.node.remote;

import de.obsidiancloud.common.OCServer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            @NotNull String executable,
            int memory,
            @NotNull List<String> jvmArgs,
            @NotNull List<String> args,
            @NotNull Map<String, String> environmentVariables,
            int port,
            @NotNull RemoteOCNode node) {
        super(
                task,
                name,
                type,
                lifecycleState,
                status,
                autoStart,
                executable,
                memory,
                jvmArgs,
                args,
                environmentVariables,
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
