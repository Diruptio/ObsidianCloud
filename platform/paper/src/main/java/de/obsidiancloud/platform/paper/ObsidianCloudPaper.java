package de.obsidiancloud.platform.paper;

import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.NetworkHandler;
import de.obsidiancloud.common.network.packets.C2SHandshakePacket;
import org.bukkit.plugin.java.JavaPlugin;

public class ObsidianCloudPaper extends JavaPlugin {
    private Connection connection;

    @Override
    public void onEnable() {
        NetworkHandler.getPacketRegistry().registerPackets();
        connect();
        new AutoReconnectTask().start();
    }

    @Override
    public void onDisable() {
        if (connection.getChannel() != null) {
            connection.getChannel().close();
        }
    }

    private void connect() {
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

    private class AutoReconnectTask extends Thread {
        public AutoReconnectTask() {
            setName("AutoReconnectTask");
            setDaemon(true);
        }

        @Override
        public void run() {
            try {
                Thread.sleep(30000);
                if (!connection.isConnected()) {
                    getLogger().info("Connection lost; trying to reconnect...");
                    connect();
                }
            } catch (InterruptedException ignored) {
            }
        }
    }
}
