package de.obsidiancloud.platform.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.platform.ObsidianCloudPlatform;
import de.obsidiancloud.platform.remote.RemoteLocalOCNode;
import de.obsidiancloud.platform.velocity.listener.PlayerListener;
import de.obsidiancloud.platform.velocity.local.LocalVelocityOCServer;
import de.obsidiancloud.platform.velocity.network.listener.ServerAddedListener;
import de.obsidiancloud.platform.velocity.network.listener.ServerRemovedListener;
import de.obsidiancloud.platform.velocity.network.listener.SyncListener;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@SuppressWarnings("unused")
@Plugin(
        id = "obsidiancloud",
        name = "ObsidianCloud",
        version = VelocityBuildConstants.VERSION,
        authors = {"Diruptio Team"})
public class ObsidianCloudVelocity {
    private static ObsidianCloudVelocity instance;
    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Path dataDirectory;
    private LocalVelocityOCServer localServer;

    @Inject
    public ObsidianCloudVelocity(
            @NotNull ProxyServer proxyServer,
            @NotNull Logger logger,
            @DataDirectory @NotNull Path dataDirectory) {
        instance = this;
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onInitialise(ProxyInitializeEvent event) {
        String serverData = System.getenv("OC_SERVER_DATA");
        localServer =
                new LocalVelocityOCServer(OCServer.TransferableServerData.fromString(serverData));
        localServer.updatePort(Integer.parseInt(System.getenv("OC_SERVER_PORT")));
        ObsidianCloudPlatform.onEnable(localServer);

        Connection connection =
                ((RemoteLocalOCNode) ObsidianCloudAPI.get().getLocalNode()).getConnection();
        connection.addPacketListener(new ServerAddedListener());
        connection.addPacketListener(new ServerRemovedListener());
        connection.addPacketListener(new SyncListener());

        proxyServer.getEventManager().register(this, new PlayerListener());
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        ObsidianCloudPlatform.onDisable();
    }

    public void registerServer(
            @NotNull OCServer.TransferableServerData data, @NotNull OCNode node, int port) {
        String name = data.name();
        unregisterServer(name);

        InetSocketAddress address = new InetSocketAddress(node.getAddress(), port);
        if (data.type() == OCServer.Type.SERVER
                && (data.linkToProxies() == null
                        || data.linkToProxies().contains(localServer.getName())
                        || (localServer.getData().task() != null
                                && data.linkToProxies().contains(localServer.getData().task())))) {
            proxyServer.registerServer(new ServerInfo(name, address));
            if (data.fallback()
                    && !proxyServer.getConfiguration().getAttemptConnectionOrder().contains(name)) {
                proxyServer.getConfiguration().getAttemptConnectionOrder().add(name);
            }
        }
    }

    public void unregisterServer(@NotNull String name) {
        Optional<RegisteredServer> registeredServer = proxyServer.getServer(name);
        if (registeredServer.isPresent()) {
            ServerInfo info = registeredServer.get().getServerInfo();
            proxyServer.unregisterServer(info);
            proxyServer.getConfiguration().getAttemptConnectionOrder().remove(name);
        }
    }

    public @NotNull ProxyServer getProxyServer() {
        return proxyServer;
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
