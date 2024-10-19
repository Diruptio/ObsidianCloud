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
                    boolean ready = server.getStatus() != OCServer.Status.NOT_READY
                            && server.getStatus() != OCServer.Status.DELETING;
                    if (isFromTask && ready) servers++;
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
            if (server instanceof LocalOCServer
                    && server.getStatus() == OCServer.Status.OFFLINE
                    && server.getData().autoStart()) {
                server.start();
            }
        }
    }
}
