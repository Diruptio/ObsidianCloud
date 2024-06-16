package de.obsidiancloud.platform.local;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LocalOCServer extends OCServer {
    public LocalOCServer(
            @Nullable String task,
            @NotNull String name,
            @NotNull Type type,
            @NotNull LifecycleState lifecycleState,
            @NotNull Status status,
            boolean autoStart,
            boolean autoDelete,
            @NotNull String executable,
            int memory,
            @NotNull List<String> jvmArgs,
            @NotNull List<String> args,
            @NotNull Map<String, String> environmentVariables,
            int port,
            @NotNull List<OCPlayer> players) {
        super(
                task,
                name,
                type,
                lifecycleState,
                status,
                autoStart,
                autoDelete,
                executable,
                memory,
                jvmArgs,
                args,
                environmentVariables,
                port,
                players);
    }

    @Override
    public void start() {}

    @Override
    public void kill() {
        System.exit(0);
    }

    @Override
    public @NotNull OCNode getNode() {
        return ObsidianCloudAPI.get().getLocalNode();
    }
}
