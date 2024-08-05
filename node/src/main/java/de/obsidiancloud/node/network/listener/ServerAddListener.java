package de.obsidiancloud.node.network.listener;

import de.obsidiancloud.common.event.EventManager;
import de.obsidiancloud.common.event.PostServerCreateEvent;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.node.network.packets.ServerAddPacket;
import org.jetbrains.annotations.NotNull;

public class ServerAddListener implements PacketListener<ServerAddPacket> {
    @Override
    public void handle(@NotNull ServerAddPacket packet, @NotNull Connection connection) {
        packet.getNode().getServers().add(packet.getServer());
        EventManager.call(new PostServerCreateEvent(packet.getServer()));
    }
}
