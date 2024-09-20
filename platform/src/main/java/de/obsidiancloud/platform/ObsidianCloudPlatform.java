package de.obsidiancloud.platform;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.NetworkHandler;
import de.obsidiancloud.common.network.listener.PlayerKickListener;
import de.obsidiancloud.common.network.listener.PlayerMessageListener;
import de.obsidiancloud.common.network.listener.ServerRemovedListener;
import de.obsidiancloud.common.network.listener.ServerUpdatedListener;
import de.obsidiancloud.common.network.packets.*;
import de.obsidiancloud.platform.local.LocalOCServer;
import de.obsidiancloud.platform.network.listener.ServerAddedListener;
import de.obsidiancloud.platform.network.listener.SyncListener;
import de.obsidiancloud.platform.network.packets.N2SSyncPacket;
import de.obsidiancloud.platform.network.packets.S2NHandshakePacket;
import de.obsidiancloud.platform.network.packets.S2NPlayerJoinPacket;
import de.obsidiancloud.platform.network.packets.S2NPlayerLeavePacket;
import de.obsidiancloud.platform.remote.RemoteLocalOCNode;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ObsidianCloudPlatform {
    private static PlatformObsidianCloudAPI api;

    public static void onEnable(LocalOCServer localServer) {
        registerPackets();
        createAPI(localServer);
        connect();
        new AutoReconnectTask().start();
        localServer.setStatus(OCServer.Status.READY);
    }

    private static void registerPackets() {
        // common
        NetworkHandler.getPacketRegistry().registerPacket(CustomMessagePacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(PlayerKickPacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(PlayerMessagePacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(ServerAddedPacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(ServerCreatePacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(ServerDeletePacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(ServerPortChangedPacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(ServerRemovedPacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(ServerStatusChangedPacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(ServerStatusChangePacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(ServerUpdatedPacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(ServerUpdatePacket.class);

        // platform
        NetworkHandler.getPacketRegistry().registerPacket(N2SSyncPacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(S2NHandshakePacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(S2NPlayerJoinPacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(S2NPlayerLeavePacket.class);
    }

    public static void onDisable() {
        if (api.getLocalNode().isConnected()) {
            api.getLocalNode().getConnection().close();
        }
    }

    private static void createAPI(LocalOCServer localServer) {
        String nodeName = System.getenv("OC_NODE_NAME");
        String nodeHost = System.getenv("OC_NODE_HOST");
        try {
            api = new PlatformObsidianCloudAPI(
                    new RemoteLocalOCNode(nodeName, InetAddress.getByName(nodeHost), localServer));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        ObsidianCloudAPI.setInstance(api);
    }

    private static void connect() {
        String nodeHost = System.getenv("OC_NODE_HOST");
        int nodePort = Integer.parseInt(System.getenv("OC_NODE_PORT"));
        Connection connection = NetworkHandler.initializeClientConnection(nodeHost, nodePort);
        api.getLocalNode().setConnection(connection);
        connection.addPacketListener(new SyncListener());
        connection.addPacketListener(new PlayerKickListener());
        connection.addPacketListener(new PlayerMessageListener());
        connection.addPacketListener(new ServerAddedListener());
        connection.addPacketListener(new ServerRemovedListener());
        connection.addPacketListener(new ServerUpdatedListener());

        String clusterKey = System.getenv("OC_CLUSTERKEY");
        S2NHandshakePacket handshakePacket = new S2NHandshakePacket();
        handshakePacket.setClusterKey(clusterKey);
        handshakePacket.setName(api.getLocalNode().getLocalServer().getName());
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
                if (!api.getLocalNode().isConnected()) {
                    NetworkHandler.getLogger().info("Connection lost; trying to reconnect...");
                    connect();
                }
            } catch (InterruptedException ignored) {
            }
        }
    }
}
