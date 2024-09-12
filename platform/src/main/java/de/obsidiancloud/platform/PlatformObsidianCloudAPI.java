package de.obsidiancloud.platform;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.OCTask;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.PacketListener;
import de.obsidiancloud.common.network.packets.ServerAddedPacket;
import de.obsidiancloud.common.network.packets.ServerCreatePacket;
import de.obsidiancloud.common.network.packets.ServerDeletePacket;
import de.obsidiancloud.common.network.packets.ServerRemovedPacket;
import de.obsidiancloud.platform.local.LocalOCServer;
import de.obsidiancloud.platform.remote.RemoteLocalOCNode;
import de.obsidiancloud.platform.remote.RemoteOCNode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public class PlatformObsidianCloudAPI extends ObsidianCloudAPI {
    private final @NotNull RemoteLocalOCNode localNode;
    private final @NotNull List<RemoteOCNode> remoteNodes = new ArrayList<>();
    private final @NotNull List<OCTask> tasks = new ArrayList<>();

    public PlatformObsidianCloudAPI(@NotNull RemoteLocalOCNode localNode) {
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
    public @NotNull RemoteLocalOCNode getLocalNode() {
        return localNode;
    }

    public @NotNull LocalOCServer getLocalServer() {
        return localNode.getLocalServer();
    }

    @Override
    public @NotNull List<OCTask> getTasks() {
        return tasks;
    }

    @Override
    public @NotNull List<OCServer> getServers() {
        List<OCServer> servers = new ArrayList<>(localNode.getServers());
        for (RemoteOCNode node : remoteNodes) {
            servers.addAll(node.getServers());
        }
        return servers;
    }

    @Override
    public @NotNull CompletableFuture<OCServer> createServer(@NotNull OCTask task) {
        String name = task.name();
        Connection connection = localNode.getConnection();

        ServerCreatePacket packet = new ServerCreatePacket();
        packet.setTask(task.name());
        connection.send(packet);

        CompletableFuture<OCServer> future =
                new CompletableFuture<OCServer>().orTimeout(20, TimeUnit.SECONDS);
        PacketListener<ServerAddedPacket> listener =
                new PacketListener<>() {
                    @Override
                    public void handle(
                            @NotNull ServerAddedPacket response, @NotNull Connection connection) {
                        if (response.getServerData() == null) {
                            connection.removePacketListener(this);
                            future.complete(null);
                        } else if (response.getServerData().name().equals(name)) {
                            connection.removePacketListener(this);
                            future.complete(getServer(response.getServerData().name()));
                        }
                    }
                };
        connection.addPacketListener(listener);
        return future;
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteServer(@NotNull OCServer server) {
        String name = server.getName();
        Connection connection = localNode.getConnection();

        ServerDeletePacket packet = new ServerDeletePacket();
        packet.setName(name);
        connection.send(packet);

        CompletableFuture<Void> future =
                new CompletableFuture<Void>().orTimeout(20, TimeUnit.SECONDS);
        PacketListener<ServerRemovedPacket> listener =
                new PacketListener<>() {
                    @Override
                    public void handle(
                            @NotNull ServerRemovedPacket response, @NotNull Connection connection) {
                        if (response.getServerName().equals(name)) {
                            connection.removePacketListener(this);
                            future.complete(null);
                        }
                    }
                };
        connection.addPacketListener(listener);
        return future;
    }
}
