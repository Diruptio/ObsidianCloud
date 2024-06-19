package de.obsidiancloud.common.network.listener;

import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.PlayerMessagePacket;
import org.jetbrains.annotations.NotNull;

public class PlayerMessageListener implements PacketListener<PlayerMessagePacket> {
    @Override
    public void handle(@NotNull PlayerMessagePacket packet, @NotNull Connection connection) {
        OCPlayer player = ObsidianCloudAPI.get().getPlayer(packet.getUUID());
        if (player != null) {
            player.sendMessage(packet.getMessage());
        }
    }
}
