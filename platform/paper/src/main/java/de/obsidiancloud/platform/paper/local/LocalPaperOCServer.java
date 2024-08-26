package de.obsidiancloud.platform.paper.local;

import de.obsidiancloud.platform.local.LocalOCServer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class LocalPaperOCServer extends LocalOCServer {
    public LocalPaperOCServer(@NotNull TransferableServerData data) {
        super(data, Status.STARTING);
    }

    @Override
    public void stop() {
        Bukkit.shutdown();
    }
}
