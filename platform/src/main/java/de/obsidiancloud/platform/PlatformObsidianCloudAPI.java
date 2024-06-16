package de.obsidiancloud.platform;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.OCTask;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.platform.remote.RemoteOCNode;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class PlatformObsidianCloudAPI extends ObsidianCloudAPI {
    private final @NotNull List<RemoteOCNode> nodes = new ArrayList<>();
    private final @NotNull List<OCTask> tasks = new ArrayList<>();
    private final @NotNull String localNode;
    private final @NotNull String localServer;

    public PlatformObsidianCloudAPI(@NotNull String localNode, @NotNull String localServer) {
        this.localNode = localNode;
        this.localServer = localServer;
    }

    public @NotNull List<RemoteOCNode> getRemoteNodes() {
        return nodes;
    }

    @Override
    public @NotNull List<OCNode> getNodes() {
        return new ArrayList<>(nodes);
    }

    @Override
    public @NotNull RemoteOCNode getLocalNode() {
        for (RemoteOCNode node : nodes) {
            if (node.getName().equals(localNode)) {
                return node;
            }
        }
        throw new IllegalStateException("Local node not found");
    }

    @Override
    public @NotNull List<OCTask> getTasks() {
        return tasks;
    }

    @Override
    public @NotNull List<OCServer> getServers() {
        List<OCServer> servers = new ArrayList<>();
        for (RemoteOCNode node : nodes) {
            servers.addAll(node.getServers());
        }
        return servers;
    }
}
