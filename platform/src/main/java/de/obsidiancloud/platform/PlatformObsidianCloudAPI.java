package de.obsidiancloud.platform;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.OCTask;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.platform.local.LocalOCServer;
import de.obsidiancloud.platform.remote.RemoteLocalOCNode;
import de.obsidiancloud.platform.remote.RemoteOCNode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
    public @NotNull OCServer createServer(@NotNull OCTask task) {
        // TODO: Send Packet to node
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteServer(@NotNull OCServer server) {
        // TODO: Send Packet to node
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
