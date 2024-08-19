package de.obsidiancloud.platform.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.platform.PlatformObsidianCloudAPI;
import de.obsidiancloud.platform.network.packets.S2NPlayerJoinPacket;
import de.obsidiancloud.platform.network.packets.S2NPlayerLeavePacket;
import de.obsidiancloud.platform.velocity.local.LocalVelocityOCPlayer;
import org.jetbrains.annotations.NotNull;

public class PlayerListener {
    private final PlatformObsidianCloudAPI api = (PlatformObsidianCloudAPI) ObsidianCloudAPI.get();

    @Subscribe
    public void onJoin(
            @NotNull LoginEvent e) { // also Post or prelogin event exists joaaaaaaaaaaaaaaaaa
        Player player = e.getPlayer();
        api.getLocalServer().getPlayers().add(new LocalVelocityOCPlayer(player));
        S2NPlayerJoinPacket packet = new S2NPlayerJoinPacket();
        packet.setUUID(player.getUniqueId());
        packet.setName(player.getUsername());
        api.getLocalNode().getConnection().send(packet);
    }

    @Subscribe
    public void onQuit(@NotNull DisconnectEvent e) {
        Player player = e.getPlayer();
        api.getLocalServer().getPlayers().remove(api.getPlayer(player.getUniqueId()));
        S2NPlayerLeavePacket packet = new S2NPlayerLeavePacket();
        packet.setUUID(player.getUniqueId());
        api.getLocalNode().getConnection().send(packet);
    }
}
