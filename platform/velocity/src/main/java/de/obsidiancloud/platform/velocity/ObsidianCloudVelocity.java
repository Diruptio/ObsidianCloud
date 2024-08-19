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
import de.obsidiancloud.platform.velocity.local.ProxyOCServer;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Plugin(id = "obsidiancloud", name = "ObsidianCloud", version = VelocityBuildConstants.VERSION)
public class ObsidianCloudVelocity {
    private static ObsidianCloudVelocity instance;
    private static ProxyServer server;
    private static Logger logger;
    private static Path dataDirectory;

    @Inject
    public ObsidianCloudVelocity(
            ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;
        ObsidianCloudVelocity.server = server;
        ObsidianCloudVelocity.logger = logger;
        ObsidianCloudVelocity.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onInitialise(ProxyInitializeEvent event) {
        String serverData = System.getenv("OC_SERVER_DATA");
        ObsidianCloudPlatform.onEnable(
                new ProxyOCServer(OCServer.TransferableServerData.fromString(serverData)));

        server.getEventManager().register(this, new PlayerListener());
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        ObsidianCloudPlatform.onDisable();
    }

    public static @NotNull ObsidianCloudVelocity getInstance() {
        return instance;
    }

    public static @NotNull Logger getLogger() {
        return logger;
    }

    public static @NotNull ProxyServer getServer() {
        return server;
    }

    public static @NotNull Path getDataDirectory() {
        return dataDirectory;
    }
}
