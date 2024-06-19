package de.obsidiancloud.platform.paper.local;

import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.platform.paper.ObsidianCloudPaper;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A player on the local server.
 */
public class LocalPaperOCPlayer extends OCPlayer {
    private final @NotNull Player player;

    public LocalPaperOCPlayer(@NotNull Player player) {
        super(player.getUniqueId(), player.getName());
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
        Plugin plugin = ObsidianCloudPaper.getInstance();
        if (message == null) {
            Bukkit.getScheduler().runTask(plugin, () -> player.kick());
        } else {
            Bukkit.getScheduler().runTask(plugin, () -> player.kick(message));
        }
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
