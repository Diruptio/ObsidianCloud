package de.obsidiancloud.common.network.listener;

import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.PlayerKickPacket;
import org.jetbrains.annotations.NotNull;

public class PlayerKickListener implements PacketListener<PlayerKickPacket> {
    @Override
    public void handle(@NotNull PlayerKickPacket packet, @NotNull Connection connection) {
        System.out.println(packet.getUUID());
        OCPlayer player = ObsidianCloudAPI.get().getPlayer(packet.getUUID());
        if (player != null) {
            player.kick(packet.getMessage());
        }
    }
}
