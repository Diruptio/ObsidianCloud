package de.obsidiancloud.node.local;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.packets.ServerUpdatePacket;
import de.obsidiancloud.node.ObsidianCloudNode;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class LocalOCServer extends OCServer {
    private Connection connection;
    private boolean screen = false;
    private Process process;

    public LocalOCServer(@NotNull OCServer.TransferableServerData data) {
        super(data, new HashSet<>());
    }

    @Override
    public void start() {
        try {
            setLifecycleState(LifecycleState.ONLINE);
            setStatus(Status.STARTING);
            ObsidianCloudNode.getLogger().info("Starting server " + getName() + "...");

            int port = getData().port();

            while (true) {
                try (ServerSocketChannel channel = ServerSocketChannel.open()) {
                    channel.bind(new InetSocketAddress(port));
                    break;
                } catch (IOException exception) {
                    if (exception.getMessage().contains("Address already in use")) {
                        port++;
                    } else throw exception;
                }
            }

            List<String> command = new ArrayList<>();
            command.add(getData().executable());
            command.add("-Xms" + getData().memory() + "M");
            command.add("-Xmx" + getData().memory() + "M");
            command.addAll(getData().jvmArgs());
            command.add("-jar");
            command.add("server.jar");
            command.addAll(getData().jvmArgs());
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(getDirectory().toFile());
            builder.environment().putAll(getData().environmentVariables());
            builder.environment().put("OC_NODE_HOST", "127.0.0.1");
            int nodePort = ObsidianCloudNode.getNetworkServer().getPort();
            builder.environment().put("OC_NODE_PORT", String.valueOf(nodePort));
            builder.environment().put("OC_CLUSTERKEY", ObsidianCloudNode.getClusterKey().get());
            builder.environment().put("OC_SERVER_DATA", getData().toString());
            process = builder.start();
            process.onExit().thenRun(this::run);
            new ScreenThread().start();
        } catch (Throwable exception) {
            exception.printStackTrace(System.err);
        }
    }

    @Override
    public void stop() {
        try {
            if (process != null && process.isAlive()) {
                ObsidianCloudNode.getLogger().info("Stopping server " + getName() + "...");
                try (BufferedWriter writer = process.outputWriter()) {
                    writer.write(getData().type().getStopCommand() + "\n");
                    writer.flush();
                }
            }
        } catch (Throwable exception) {
            exception.printStackTrace(System.err);
        }
    }

    @Override
    public void kill() {
        try {
            if (process != null && process.isAlive()) {
                ObsidianCloudNode.getLogger().info("Killing server " + getName() + "...");
                process.destroy();
            }
        } catch (Throwable exception) {
            exception.printStackTrace(System.err);
        }
    }

    @Override
    public void setLifecycleState(@NotNull LifecycleState lifecycleState) {
        OCServer.TransferableServerData data = getData();
        data =
                new TransferableServerData(
                        data.task(),
                        data.name(),
                        data.type(),
                        lifecycleState,
                        data.status(),
                        data.autoStart(),
                        data.autoDelete(),
                        data.executable(),
                        data.memory(),
                        data.jvmArgs(),
                        data.args(),
                        data.environmentVariables(),
                        data.port());
        updateData(data);
        ServerUpdatePacket packet = new ServerUpdatePacket();
        packet.setServerData(data);
        for (Connection connection : ObsidianCloudNode.getNetworkServer().getConnections()) {
            connection.send(packet);
        }
    }

    @Override
    public void setStatus(@NotNull Status status) {
        OCServer.TransferableServerData data = getData();
        data =
                new TransferableServerData(
                        data.task(),
                        data.name(),
                        data.type(),
                        data.lifecycleState(),
                        status,
                        data.autoStart(),
                        data.autoDelete(),
                        data.executable(),
                        data.memory(),
                        data.jvmArgs(),
                        data.args(),
                        data.environmentVariables(),
                        data.port());
        updateData(data);
        ServerUpdatePacket packet = new ServerUpdatePacket();
        packet.setServerData(data);
        for (Connection connection : ObsidianCloudNode.getNetworkServer().getConnections()) {
            connection.send(packet);
        }
    }

    @Override
    public @NotNull OCNode getNode() {
        return ObsidianCloudAPI.get().getLocalNode();
    }

    public @NotNull Path getDirectory() {
        return Path.of("servers").resolve(getName());
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(@NotNull Connection connection) {
        this.connection = connection;
    }

    private void run() {
        ObsidianCloudNode.getLogger().info("Server " + getName() + " stopped");
        setLifecycleState(LifecycleState.OFFLINE);
        setStatus(Status.OFFLINE);
    }

    public boolean isScreen() {
        return screen;
    }

    public void setScreen(boolean screen) {
        this.screen = screen;
    }

    public Process getProcess() {
        return process;
    }

    private class ScreenThread extends Thread {
        public ScreenThread() {
            setName("ScreenThread");
            setDaemon(true);
        }

        @Override
        public void run() {
            try (BufferedReader reader = process.inputReader()) {
                while (process.isAlive()) {
                    String line = reader.readLine();
                    if (screen && line != null) {
                        ObsidianCloudNode.getLogger()
                                .info("[%s] %s".formatted(LocalOCServer.this.getName(), line));
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    }
}
