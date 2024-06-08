package de.obsidiancloud.protocol.TEST;

import de.obsidiancloud.protocol.NetworkHandler;
import de.obsidiancloud.protocol.packets.HandshakePacket;
import de.obsidiancloud.protocol.packets.TestPacket;
import de.obsidiancloud.protocol.pipeline.ConnectionHandler;

/**
 * @author Miles
 * @since 08.06.2024
 */
public class Node2Test {

    public static void main(String[] args) {
        ConnectionHandler node2Handler = NetworkHandler.initializeClientConnection("node-2", "localhost", 1337);
        HandshakePacket node2Packet = new HandshakePacket();
        node2Packet.setId(node2Handler.getId());
        node2Handler.send(node2Packet);

        NetworkHandler.getPacketRegistry().registerPacketListener(new TestPacketListener());

        TestPacket p = new TestPacket("node-1");
        p.setName("test ;:D");
        node2Handler.send(p);
    }
}
