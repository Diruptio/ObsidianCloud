package de.obsidiancloud.protocol.TEST;

import de.obsidiancloud.protocol.NetworkHandler;
import de.obsidiancloud.protocol.packets.HandshakePacket;
import de.obsidiancloud.protocol.pipeline.ConnectionHandler;
import de.obsidiancloud.protocol.failsafe.AutoReconnect;

import java.util.function.Consumer;

/**
 * @author Miles
 * @since 08.06.2024
 */
public class Node1Test {

    private static final String CONNECTION_ID = "node-1";
    private static final String HOST = "localhost";
    private static final int PORT = 1337;

    private static ConnectionHandler node1Handler;

    public static void main(String[] args) {
        node1Handler = NetworkHandler.initializeClientConnection(CONNECTION_ID, HOST, PORT);
        HandshakePacket node1Packet = new HandshakePacket();
        node1Packet.setId(node1Handler.getId());
        node1Handler.send(node1Packet);

        NetworkHandler.getPacketRegistry().registerPacketListener(new TestPacketListener());

        AutoReconnect autoReconnect = new AutoReconnect(node1Handler, HOST, setConnection());
        autoReconnect.listen();
    }

    private static Consumer<ConnectionHandler> setConnection() {
        return newConnection -> {
            if (newConnection != null) {
                node1Handler = newConnection;
            }
        };
    }
}
