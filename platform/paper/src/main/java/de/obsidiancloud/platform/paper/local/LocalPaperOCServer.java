package de.obsidiancloud.platform.paper.local;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.platform.local.LocalOCServer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class LocalPaperOCServer extends LocalOCServer {
    public LocalPaperOCServer(@NotNull OCServer.TransferableServerData data) {
        super(
                new TransferableServerData(
                        data.task(),
                        data.name(),
                        Type.PAPER,
                        LifecycleState.ONLINE,
                        Status.READY,
                        data.autoStart(),
                        data.autoDelete(),
                        data.executable(),
                        data.memory(),
                        data.jvmArgs(),
                        data.args(),
                        data.environmentVariables(),
                        data.port()));
    }

    @Override
    public void stop() {
        Bukkit.shutdown();
    }
}
