package de.obsidiancloud.platform.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import de.obsidiancloud.platform.ObsidianCloudPlatform;
import de.obsidiancloud.platform.velocity.listener.PlayerListener;
import de.obsidiancloud.platform.velocity.proxy.ProxyOCServer;
import java.nio.file.Path;
import org.slf4j.Logger;

@Plugin(id = "ocvelocity", name = "OCVelocity", version = BuildConstants.VERSION)
public class OCVelocity {
    private static ProxyServer server;
    private static Logger logger;
    private static Path dataDirectory;

    @Inject
    public OCVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        OCVelocity.server = server;
        OCVelocity.logger = logger;
        OCVelocity.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onInitialise(ProxyInitializeEvent e) {
        server.getEventManager().register(this, new PlayerListener());
        String proxyTask = System.getenv("OC_PROXY_TASK");
        String proxyName = System.getenv("OC_PROXY_NAME");
        boolean serverAutoStart = Boolean.parseBoolean(System.getenv("OC_PROXY_AUTOSTART"));
        ObsidianCloudPlatform.onEnable(new ProxyOCServer(proxyTask, proxyName, serverAutoStart));
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent e) {
        ObsidianCloudPlatform.onDisable();
    }

    public static Logger getLogger() {
        return logger;
    }

    public static ProxyServer getServer() {
        return server;
    }

    public static Path getDataDirectory() {
        return dataDirectory;
    }
}
