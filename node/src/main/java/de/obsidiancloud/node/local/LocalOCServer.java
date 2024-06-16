package de.obsidiancloud.node.local;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.node.ObsidianCloudNode;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocalOCServer extends OCServer {
    private boolean screen = false;
    private Process process;

    public LocalOCServer(
            @Nullable String task,
            @NotNull String name,
            @NotNull Type type,
            @NotNull LifecycleState lifecycleState,
            @NotNull Status status,
            boolean autoStart,
            boolean autoDelete,
            @NotNull String executable,
            int memory,
            @NotNull List<String> jvmArgs,
            @NotNull List<String> args,
            @NotNull Map<String, String> environmentVariables,
            int port) {
        super(
                task,
                name,
                type,
                lifecycleState,
                status,
                autoStart,
                autoDelete,
                executable,
                memory,
                jvmArgs,
                args,
                environmentVariables,
                port,
                new ArrayList<>());
    }

    @Override
    public void start() {
        try {
            setLifecycleState(LifecycleState.ONLINE);
            setStatus(Status.STARTING);
            ObsidianCloudNode.getLogger().info("Starting server " + getName() + "...");

            int port = getPort();
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
            command.add(getExecutable());
            command.add("-Xms" + getMemory() + "M");
            command.add("-Xmx" + getMemory() + "M");
            command.addAll(getJvmArgs());
            command.add("-jar");
            command.add("server.jar");
            command.addAll(getArgs());
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(getDirectory().toFile());
            builder.environment().putAll(getEnvironmentVariables());
            builder.environment().put("OC_NODE_HOST", "127.0.0.1");
            builder.environment()
                    .put(
                            "OC_NODE_PORT",
                            String.valueOf(ObsidianCloudNode.getNetworkServer().getPort()));
            builder.environment().put("OC_CLUSTERKEY", "testClusterKey");
            builder.environment().put("OC_SERVER_NAME", getName());
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
                    writer.write(getType().getStopCommand() + "\n");
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
    public @NotNull OCNode getNode() {
        return ObsidianCloudAPI.get().getLocalNode();
    }

    public @NotNull Path getDirectory() {
        return Path.of("servers").resolve(getName());
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
