package de.obsidiancloud.common.network.listener;

import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.C2SHandshakePacket;

public class C2SHandshakeListener implements PacketListener<C2SHandshakePacket> {
    @Override
    public void handle(C2SHandshakePacket packet, Connection connection) {
        System.out.println("Received C2S handshake packet: " + packet.getClusterKey());
    }
}
