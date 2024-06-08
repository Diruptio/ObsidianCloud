package de.obsidiancloud.protocol.TEST;

import de.obsidiancloud.protocol.NetworkHandler;
import de.obsidiancloud.protocol.packets.HandshakePacket;
import de.obsidiancloud.protocol.pipeline.ConnectionHandler;

/**
 * @author Miles
 * @since 08.06.2024
 */
public class Node1Test {

    public static void main(String[] args) {
        ConnectionHandler node1Handler = NetworkHandler.initializeClientConnection("node-1", "localhost", 1337);
        HandshakePacket node1Packet = new HandshakePacket();
        node1Packet.setId(node1Handler.getId());
        node1Handler.send(node1Packet);

        NetworkHandler.getPacketRegistry().registerPacketListener(new TestPacketListener());
    }
}
