package de.obsidiancloud.node.local;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.packets.ServerStatusChangedPacket;
import de.obsidiancloud.node.ObsidianCloudNode;
import de.obsidiancloud.node.util.AikarsFlags;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class LocalOCServer extends OCServer {
    private Connection connection;
    private boolean screen = false;
    private Process process;

    public LocalOCServer(@NotNull TransferableServerData data, @NotNull Status status) {
        super(data, status, new ArrayList<>());
    }

    @Override
    public void start() {
        try {
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
            if (getData().type() != Type.CUSTOM) {
                command.add("-Xms" + getData().memory() + "M");
                command.add("-Xmx" + getData().memory() + "M");
                command.addAll(getData().jvmArgs());
                command.add("-jar");
                command.add("server.jar");
                command.addAll(getData().args());
            }
            for (int i = 0; i < command.size(); i++) {
                String arg = command.get(i);
                if (arg.equals("%AIKARS_FLAGS%")) {
                    command.remove(i);
                    command.addAll(i, List.of(AikarsFlags.DEFAULT));
                } else {
                    command.set(i, arg.replace("%SERVER_PORT%", String.valueOf(port)));
                }
            }
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(getDirectory().toFile());
            builder.environment().putAll(getData().environmentVariables());
            builder.environment().put("OC_NODE_HOST", "127.0.0.1");
            int nodePort = ObsidianCloudNode.getNetworkServer().getPort();
            builder.environment().put("OC_NODE_PORT", String.valueOf(nodePort));
            builder.environment().put("OC_CLUSTERKEY", ObsidianCloudNode.getClusterKey().get());
            builder.environment().put("OC_SERVER_DATA", getData().toString());
            process = builder.start();
            process.onExit().thenRun(this::stopped);
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
                Platform platform = getData().platform();
                if (platform == null) {
                    process.destroy();
                } else {
                    try (BufferedWriter writer = process.outputWriter()) {
                        writer.write(getData().platform().stopCommand() + "\n");
                        writer.flush();
                    }
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
    public void setStatus(@NotNull Status status) {
        this.status = status;
        ServerStatusChangedPacket packet = new ServerStatusChangedPacket();
        packet.setName(getName());
        packet.setStatus(status);
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

    private void stopped() {
        ObsidianCloudNode.getLogger().info("Server " + getName() + " stopped");
        setStatus(Status.OFFLINE);
    }

    public boolean isScreen() {
        return screen;
    }

    public void setScreen(boolean screen) {
        this.screen = screen;
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
