package de.obsidiancloud.node.threads;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.OCTask;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.event.EventManager;
import de.obsidiancloud.node.event.ServerCreateEvent;
import de.obsidiancloud.node.local.LocalOCNode;
import de.obsidiancloud.node.local.LocalOCServer;
import org.jetbrains.annotations.NotNull;

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
                    boolean isFromTask = task.name().equals(server.getTask());
                    boolean isNotReady = server.getStatus() == OCServer.Status.NOT_READY;
                    if (isFromTask && !isNotReady) servers++;
                }
            }

            // Create servers
            while (servers < task.minAmount()) {
                createServer(task);
                servers++;
            }
        }
    }

    /**
     * Creates a server from the given task.
     *
     * @param task The task to create the server with.
     */
    public void createServer(@NotNull OCTask task) {
        // Find name
        int n = 1;
        while (api.getServer(task.name() + "-" + n) != null) {
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

        ServerCreateEvent event = new ServerCreateEvent(server);
        EventManager.call(event);
        if (event.isCancelled()) return;

        localNode.getServers().add(server);
        new ServerCreateThread(server, task.templates()).start();
    }

    private void startServers() {
        for (OCServer server : localNode.getServers()) {
            if (server.getLifecycleState() == OCServer.LifecycleState.OFFLINE
                    && server.isAutoStart()) {
                server.start();
            }
        }
    }

    private void deleteServers() {
        for (OCServer server : localNode.getServers()) {
            LocalOCServer localServer = (LocalOCServer) server;
            if (server.getLifecycleState() == OCServer.LifecycleState.OFFLINE
                    && localServer.isAutoDelete()) {
                new ServerDeleteThread(localServer).run();
            }
        }
    }
}
