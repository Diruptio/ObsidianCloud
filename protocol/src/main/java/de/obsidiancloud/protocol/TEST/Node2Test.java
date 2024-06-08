package de.obsidiancloud.protocol.TEST;

import de.obsidiancloud.protocol.NetworkHandler;
import de.obsidiancloud.protocol.failsafe.AutoReconnect;
import de.obsidiancloud.protocol.packets.HandshakePacket;
import de.obsidiancloud.protocol.packets.TestPacket;
import de.obsidiancloud.protocol.pipeline.ConnectionHandler;
import org.checkerframework.checker.units.qual.C;

import java.util.function.Consumer;

/**
 * @author Miles
 * @since 08.06.2024
 */
public class Node2Test {

    private static final String CONNECTION_ID = "node-2";
    private static final String HOST = "localhost";
    private static final int PORT = 1337;

    private static ConnectionHandler node2Handler;

    public static void main(String[] args) {
        ConnectionHandler node2Handler = NetworkHandler.initializeClientConnection(CONNECTION_ID, HOST, PORT);
        HandshakePacket node2Packet = new HandshakePacket();
        node2Packet.setId(node2Handler.getId());
        node2Handler.send(node2Packet);

        NetworkHandler.getPacketRegistry().registerPacketListener(new TestPacketListener());

        AutoReconnect autoReconnect = new AutoReconnect(node2Handler, HOST, setConnection());
        autoReconnect.listen();

        TestPacket p = new TestPacket("node-1");
        p.setName("test ;:D");
        node2Handler.send(p);
    }

    private static Consumer<ConnectionHandler> setConnection() {
        return newConnection -> {
            if (newConnection != null) {
                node2Handler = newConnection;
            }
        };
    }
}
