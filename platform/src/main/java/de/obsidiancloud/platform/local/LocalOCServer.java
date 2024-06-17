package de.obsidiancloud.platform.local;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.platform.remote.RemoteLocalOCNode;
import java.util.ArrayList;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LocalOCServer extends OCServer {
    public LocalOCServer(
            @Nullable String task,
            @NotNull String name,
            @NotNull Type type,
            @NotNull LifecycleState lifecycleState,
            @NotNull Status status,
            boolean autoStart) {
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
                new ArrayList<>());
    }

    @Override
    public void start() {}

    @Override
    public void kill() {
        System.exit(0);
    }

    @Override
    public @NotNull RemoteLocalOCNode getNode() {
        return (RemoteLocalOCNode) ObsidianCloudAPI.get().getLocalNode();
    }
}
