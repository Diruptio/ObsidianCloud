package de.obsidiancloud.node.threads;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.OCTask;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.node.local.LocalOCNode;
import de.obsidiancloud.node.local.LocalOCServer;

public class NodeThread extends Thread {
    private final ObsidianCloudAPI api = ObsidianCloudAPI.get();
    private final LocalOCNode localNode = (LocalOCNode) api.getLocalNode();

    @Override
    public void run() {
        while (true) {
            try {
                createServers();
                startServers();
                deleteServers();
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
                return;
            }
        }
    }

    private void createServers() {
        for (OCTask task : api.getTasks()) {
            // Count servers
            int servers = 0;
            synchronized (localNode.getServers()) {
                for (OCServer server : localNode.getServers()) {
                    boolean isFromTask = task.name().equals(server.getData().task());
                    boolean isNotReady = server.getData().status() == OCServer.Status.NOT_READY;
                    if (isFromTask && !isNotReady) servers++;
                }
            }

            // Create servers
            while (servers < task.minAmount()) {
                ObsidianCloudAPI.get().createServer(task);
                servers++;
            }
        }
    }

    private void startServers() {
        for (OCServer server : localNode.getServers()) {
            if (server.getData().lifecycleState() == OCServer.LifecycleState.OFFLINE
                    && server.getData().autoStart()) {
                server.start();
            }
        }
    }

    private void deleteServers() {
        for (OCServer server : localNode.getServers()) {
            LocalOCServer localServer = (LocalOCServer) server;
            if (server.getData().lifecycleState() == OCServer.LifecycleState.OFFLINE
                    && localServer.getData().autoDelete()) {
                ObsidianCloudAPI.get().deleteServer(server);
            }
        }
    }
}
