package de.obsidiancloud.platform.velocity.proxy;

import de.obsidiancloud.platform.local.LocalOCServer;
import de.obsidiancloud.platform.velocity.OCVelocity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProxyOCServer extends LocalOCServer {
    public ProxyOCServer(@Nullable String task, @NotNull String name, boolean autoStart) {
        super(task, name, Type.PAPER, LifecycleState.ONLINE, Status.READY, autoStart);
    }

    @Override
    public void stop() {
        OCVelocity.getServer().shutdown();
    }
}
