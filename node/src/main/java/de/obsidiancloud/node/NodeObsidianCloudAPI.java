package de.obsidiancloud.node;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.OCTask;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.node.local.LocalOCNode;
import de.obsidiancloud.node.remote.RemoteOCNode;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class NodeObsidianCloudAPI extends ObsidianCloudAPI {
    private final List<RemoteOCNode> remoteNodes = new ArrayList<>();
    private final LocalOCNode localNode;
    private final List<OCTask> tasks = new ArrayList<>();

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
            servers.addAll(node.getServers());
        }
        return servers;
    }
}
