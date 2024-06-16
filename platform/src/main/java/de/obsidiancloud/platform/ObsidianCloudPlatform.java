package de.obsidiancloud.platform;

import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.NetworkHandler;
import de.obsidiancloud.common.network.packets.C2SHandshakePacket;
import org.jetbrains.annotations.NotNull;

public class ObsidianCloudPlatform {
    private static Connection connection;

    public static void onEnable() {
        NetworkHandler.getPacketRegistry().registerPackets();
        connect();
        new AutoReconnectTask().start();
        createAPI();
    }

    public static void onDisable() {
        if (connection.getChannel() != null) {
            connection.getChannel().close();
        }
    }

    private static void createAPI() {
        String localNode = System.getenv("OC_NODE_NAME");
        String localServer = System.getenv("OC_SERVER_NAME");
        ObsidianCloudAPI.setInstance(new PlatformObsidianCloudAPI(localNode, localServer));
    }

    private static void connect() {
        String host = System.getenv("OC_NODE_HOST");
        int port = Integer.parseInt(System.getenv("OC_NODE_PORT"));
        connection = NetworkHandler.initializeClientConnection(host, port);

        String clusterKey = System.getenv("OC_CLUSTERKEY");
        String name = System.getenv("OC_SERVER_NAME");
        C2SHandshakePacket handshakePacket = new C2SHandshakePacket();
        handshakePacket.setClusterKey(clusterKey);
        handshakePacket.setName(name);
        connection.send(handshakePacket);
    }

    private static class AutoReconnectTask extends Thread {
        public AutoReconnectTask() {
            setName("AutoReconnectTask");
            setDaemon(true);
        }

        @Override
        public void run() {
            try {
                Thread.sleep(30000);
                if (!connection.isConnected()) {
                    NetworkHandler.getLogger().info("Connection lost; trying to reconnect...");
                    connect();
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    public static @NotNull Connection getConnection() {
        return connection;
    }

    public static void setConnection(@NotNull Connection connection) {
        ObsidianCloudPlatform.connection = connection;
    }
}
