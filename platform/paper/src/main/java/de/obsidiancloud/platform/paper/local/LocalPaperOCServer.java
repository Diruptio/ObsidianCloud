package de.obsidiancloud.platform.paper.local;

import de.obsidiancloud.platform.local.LocalOCServer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocalPaperOCServer extends LocalOCServer {
    public LocalPaperOCServer(@Nullable String task, @NotNull String name, boolean autoStart) {
        super(task, name, Type.PAPER, LifecycleState.ONLINE, Status.READY, autoStart);
    }

    @Override
    public void stop() {
        Bukkit.shutdown();
    }
}
