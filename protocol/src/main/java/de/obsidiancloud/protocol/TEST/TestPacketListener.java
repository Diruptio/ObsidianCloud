package de.obsidiancloud.protocol.TEST;

import de.obsidiancloud.protocol.PacketListener;
import de.obsidiancloud.protocol.packets.TestPacket;
import de.obsidiancloud.protocol.pipeline.ConnectionHandler;

/**
 * @author Miles
 * @since 02.06.2024
 */
public class TestPacketListener implements PacketListener<TestPacket> {

    @Override
    public void handle(TestPacket packet, ConnectionHandler connection) {
        System.out.println("Received TestPacket: " + packet.getName());
    }
}
