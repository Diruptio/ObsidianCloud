package de.obsidiancloud.platform.velocity.local;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.platform.velocity.ObsidianCloudVelocity;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A player on the local server. */
public class LocalVelocityOCPlayer extends OCPlayer {
    private final @NotNull Player player;

    public LocalVelocityOCPlayer(@NotNull Player player) {
        super(player.getUniqueId(), player.getUsername());
        this.player = player;
    }

    @Override
    public @Nullable OCServer getProxy() {
        for (OCServer server : ObsidianCloudAPI.get().getServers()) {
            if (server.getData().type() == OCServer.Type.PROXY
                    && server.getPlayers().contains(this)) {
                return server;
            }
        }
        return null;
    }

    @Override
    public @Nullable OCServer getServer() {
        return ObsidianCloudAPI.get().getLocalServer();
    }

    @Override
    public void connect(@NotNull OCServer server) {
        ProxyServer velocityServer = ObsidianCloudVelocity.getInstance().getProxyServer();
        Optional<Player> player = velocityServer.getPlayer(getUUID());
        if (player.isPresent()) {
            Optional<RegisteredServer> registeredServer =
                    velocityServer.getServer(server.getName());
            registeredServer.ifPresent(
                    value -> player.get().createConnectionRequest(value).connect());
        }
    }

    @Override
    public void kick(@Nullable Component message) {
        player.disconnect(message);
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        player.sendMessage(message);
    }

    @Override
    public String getCommandPrefix() {
        return "/cloud ";
    }
}
