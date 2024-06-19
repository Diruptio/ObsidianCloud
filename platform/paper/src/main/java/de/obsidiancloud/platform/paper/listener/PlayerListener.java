package de.obsidiancloud.platform.paper.listener;

import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.platform.PlatformObsidianCloudAPI;
import de.obsidiancloud.platform.network.packets.S2NPlayerJoinPacket;
import de.obsidiancloud.platform.network.packets.S2NPlayerLeavePacket;
import de.obsidiancloud.platform.paper.local.LocalPaperOCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final PlatformObsidianCloudAPI api = (PlatformObsidianCloudAPI) ObsidianCloudAPI.get();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        api.getLocalServer().getPlayers().add(new LocalPaperOCPlayer(player));
        S2NPlayerJoinPacket packet = new S2NPlayerJoinPacket();
        packet.setUUID(player.getUniqueId());
        packet.setName(player.getName());
        api.getLocalNode().getConnection().send(packet);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        api.getLocalServer().getPlayers().remove(api.getPlayer(player.getUniqueId()));
        S2NPlayerLeavePacket packet = new S2NPlayerLeavePacket();
        packet.setUUID(player.getUniqueId());
        api.getLocalNode().getConnection().send(packet);
    }
}
