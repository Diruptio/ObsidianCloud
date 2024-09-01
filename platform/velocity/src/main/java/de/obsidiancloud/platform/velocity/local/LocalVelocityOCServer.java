package de.obsidiancloud.platform.velocity.local;

import de.obsidiancloud.platform.local.LocalOCServer;
import de.obsidiancloud.platform.velocity.ObsidianCloudVelocity;
import org.jetbrains.annotations.NotNull;

public class LocalVelocityOCServer extends LocalOCServer {
    public LocalVelocityOCServer(@NotNull TransferableServerData data) {
        super(data, Status.STARTING);
    }

    @Override
    public void stop() {
        ObsidianCloudVelocity.getInstance().getServer().shutdown();
    }
}
