package de.obsidiancloud.platform.velocity.proxy;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.platform.velocity.OCVelocity;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProxiedOCPlayer extends OCPlayer {
    private final @NotNull Player player;

    public ProxiedOCPlayer(@NotNull Player player) {
        super(player.getUniqueId(), player.getUsername());
        this.player = player;
    }

    @Override
    public @Nullable OCServer getProxy() {
        for (OCServer server : ObsidianCloudAPI.get().getServers()) {
            if (server.getType().isProxy() && server.getPlayers().contains(this)) {
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
        // TODO: Send packet to getNode() to connect player to server
    }

    @Override
    public void kick(@Nullable Component message) {
        ProxyServer plugin = OCVelocity.getServer();
        plugin.getScheduler()
                .buildTask(
                        plugin,
                        () ->
                                player.disconnect(
                                        Objects.requireNonNullElseGet(message, Component::empty)));
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
