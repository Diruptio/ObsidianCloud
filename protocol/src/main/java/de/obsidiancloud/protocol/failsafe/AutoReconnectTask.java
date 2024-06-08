package de.obsidiancloud.protocol.failsafe;

import de.obsidiancloud.protocol.NetworkHandler;
import de.obsidiancloud.protocol.packets.HandshakePacket;
import de.obsidiancloud.protocol.pipeline.ConnectionHandler;
import lombok.AllArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author Miles
 * @since 08.06.2024
 */
@AllArgsConstructor
public class AutoReconnectTask {

    private static final int PORT_RANGE_START = 1000;
    private static final int PORT_RANGE_END = 10000;

    private ConnectionHandler connection;
    private final String host;

    public CompletableFuture<ConnectionHandler> check() {
        if (connection.getChannel() == null
                || !connection.getChannel().isActive()) {
            return CompletableFuture.completedFuture(this.reconnect());
        }

        return CompletableFuture.completedFuture(null);
    }

    private ConnectionHandler reconnect() {
        for (int port = PORT_RANGE_START; port < PORT_RANGE_END; port++) {
            System.out.println("[" + connection.getId() + "] Auto-Reconnect: trying port " + port);
            try {
                ConnectionHandler newConnection = NetworkHandler.initializeClientConnection(connection.getId(), host, port);
                if (newConnection.getChannel() == null || !newConnection.getChannel().isActive()) {
                    continue;
                }

                System.out.println("[" + connection.getId() + "] Auto-Reconnect: connected to port " + port);
                this.sendHandshake(newConnection);
                connection = newConnection;
                return newConnection;
            } catch (Exception ignored) {
                continue;
            }
        }

        return null;
    }

    private void sendHandshake(ConnectionHandler connection) {
        HandshakePacket node1Packet = new HandshakePacket();
        node1Packet.setId(connection.getId());
        connection.send(node1Packet);
    }
}