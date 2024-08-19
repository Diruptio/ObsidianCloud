package de.obsidiancloud.platform.velocity.local;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.platform.local.LocalOCServer;
import de.obsidiancloud.platform.velocity.ObsidianCloudVelocity;
import org.jetbrains.annotations.NotNull;

public class ProxyOCServer extends LocalOCServer {
    public ProxyOCServer(@NotNull OCServer.TransferableServerData data) {
        super(data);
    }

    @Override
    public void stop() {
        ObsidianCloudVelocity.getServer().shutdown();
    }
}
