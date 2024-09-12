package de.obsidiancloud.node;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.OCTask;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.event.EventManager;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerDeletePacket;
import de.obsidiancloud.common.network.packets.ServerRemovedPacket;
import de.obsidiancloud.node.event.ServerCreateEvent;
import de.obsidiancloud.node.local.LocalOCNode;
import de.obsidiancloud.node.local.LocalOCServer;
import de.obsidiancloud.node.remote.RemoteOCNode;
import de.obsidiancloud.node.remote.RemoteOCServer;
import de.obsidiancloud.node.threads.ServerCreateThread;
import de.obsidiancloud.node.threads.ServerDeleteThread;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public class NodeObsidianCloudAPI extends ObsidianCloudAPI {
    private final @NotNull LocalOCNode localNode;
    private final @NotNull List<RemoteOCNode> remoteNodes = new ArrayList<>();
    private final @NotNull List<OCTask> tasks = new ArrayList<>();

    public NodeObsidianCloudAPI(@NotNull LocalOCNode localNode) {
        this.localNode = localNode;
    }

    public @NotNull List<RemoteOCNode> getRemoteNodes() {
        return remoteNodes;
    }

    @Override
    public @NotNull List<OCNode> getNodes() {
        List<OCNode> nodes = new ArrayList<>();
        nodes.add(localNode);
        nodes.addAll(remoteNodes);
        return nodes;
    }

    @Override
    public @NotNull LocalOCNode getLocalNode() {
        return localNode;
    }

    @Override
    public @NotNull List<OCTask> getTasks() {
        return tasks;
    }

    @Override
    public @NotNull List<OCServer> getServers() {
        List<OCServer> servers = new ArrayList<>(localNode.getServers());
        for (RemoteOCNode node : remoteNodes) {
            if (node.isConnected()) {
                servers.addAll(node.getServers());
            }
        }
        return servers;
    }

    @Override
    public @NotNull CompletableFuture<OCServer> createServer(@NotNull OCTask task) {
        // Find an available name
        int n = 1;
        while (getServer(task.name() + "-" + n) != null) {
            n++;
        }
        String name = task.name() + "-" + n;

        // Create a server instance
        LocalOCServer server =
                new LocalOCServer(
                        new OCServer.TransferableServerData(
                                task.name(),
                                name,
                                task.type(),
                                task.platform(),
                                task.staticServer(),
                                task.autoStart(),
                                task.executable(),
                                task.memory(),
                                task.jvmArgs(),
                                task.args(),
                                task.environmentVariables(),
                                task.port()),
                        OCServer.Status.CREATING);

        ServerCreateEvent event = new ServerCreateEvent(server);
        EventManager.call(event);
        if (event.isCancelled()) {
            return CompletableFuture.completedFuture(null);
        }

        localNode.getServers().add(server);
        new ServerCreateThread(server, task.templates()).start();

        // Return the server
        return CompletableFuture.completedFuture(server);
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteServer(@NotNull OCServer server) {
        String name = server.getName();

        if (server instanceof RemoteOCServer remoteServer) {
            Connection connection = remoteServer.getNode().getConnection();

            ServerDeletePacket packet = new ServerDeletePacket();
            packet.setName(name);
            connection.send(packet);

            CompletableFuture<Void> future =
                    new CompletableFuture<Void>().orTimeout(20, TimeUnit.SECONDS);
            PacketListener<ServerRemovedPacket> listener =
                    new PacketListener<>() {
                        @Override
                        public void handle(
                                @NotNull ServerRemovedPacket response,
                                @NotNull Connection connection) {
                            if (response.getServerName().equals(name)) {
                                connection.removePacketListener(this);
                                future.complete(null);
                            }
                        }
                    };
            connection.addPacketListener(listener);
            return future;
        } else {
            localNode.getServers().remove(server);
            new Thread(new ServerDeleteThread((LocalOCServer) server)).start();

            ServerRemovedPacket packet = new ServerRemovedPacket();
            packet.setServerName(name);
            for (Connection connection : ObsidianCloudNode.getNetworkServer().getConnections()) {
                connection.send(packet);
            }

            CompletableFuture<Void> future = new CompletableFuture<>();
            future.complete(null);
            return future;
        }
    }

    public @NotNull Optional<OCServer> getServer(Connection connection) {
        for (OCServer server : localNode.getServers()) {
            if (((LocalOCServer) server).getConnection().equals(connection)) {
                return Optional.of(server);
            }
        }
        return Optional.empty();
    }
}
