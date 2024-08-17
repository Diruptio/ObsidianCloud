package de.obsidiancloud.node;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.OCTask;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.node.local.LocalOCNode;
import de.obsidiancloud.node.local.LocalOCServer;
import de.obsidiancloud.node.remote.RemoteOCNode;
import de.obsidiancloud.node.threads.ServerCreateThread;
import de.obsidiancloud.node.threads.ServerDeleteThread;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
    public @NotNull OCServer createServer(@NotNull OCTask task) {
        // Find name
        int n = 1;
        while (getServer(task.name() + "-" + n) != null) {
            n++;
        }
        String name = task.name() + "-" + n;

        // Create server instance
        LocalOCServer server =
                new LocalOCServer(
                        task.name(),
                        name,
                        task.type(),
                        OCServer.LifecycleState.CREATING,
                        OCServer.Status.OFFLINE,
                        task.autoStart(),
                        task.autoDelete(),
                        task.executable(),
                        task.memory(),
                        task.jvmArgs(),
                        task.args(),
                        task.environmentVariables(),
                        task.port());

        localNode.getServers().add(server);
        new ServerCreateThread(server, task.templates()).start();
        return server;
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteServer(@NotNull OCServer server) {
        return CompletableFuture.runAsync(new ServerDeleteThread((LocalOCServer) server));
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
