package de.obsidiancloud.platform.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.platform.ObsidianCloudPlatform;
import de.obsidiancloud.platform.velocity.listener.PlayerListener;
import de.obsidiancloud.platform.velocity.local.LocalVelocityOCServer;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@SuppressWarnings("unused")
@Plugin(id = "obsidiancloud", name = "ObsidianCloud", version = VelocityBuildConstants.VERSION)
public class ObsidianCloudVelocity {
    private static ObsidianCloudVelocity instance;
    private final @NotNull ProxyServer server;
    private final @NotNull Logger logger;
    private final @NotNull Path dataDirectory;

    @Inject
    public ObsidianCloudVelocity(
            @NotNull ProxyServer server,
            @NotNull Logger logger,
            @DataDirectory @NotNull Path dataDirectory) {
        instance = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onInitialise(ProxyInitializeEvent event) {
        String serverData = System.getenv("OC_SERVER_DATA");
        ObsidianCloudPlatform.onEnable(
                new LocalVelocityOCServer(OCServer.TransferableServerData.fromString(serverData)));

        server.getEventManager().register(this, new PlayerListener());
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        ObsidianCloudPlatform.onDisable();
    }

    public @NotNull ProxyServer getServer() {
        return server;
    }

    public @NotNull Logger getLogger() {
        return logger;
    }

    public @NotNull Path getDataDirectory() {
        return dataDirectory;
    }

    public static @NotNull ObsidianCloudVelocity getInstance() {
        return instance;
    }
}
