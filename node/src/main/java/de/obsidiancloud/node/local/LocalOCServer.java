package de.obsidiancloud.node.local;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.node.Node;
import de.obsidiancloud.node.util.AikarsFlags;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocalOCServer extends OCServer implements Runnable {
    private boolean screen = false;
    private Process process;

    public LocalOCServer(
            @Nullable String task,
            @NotNull String name,
            @NotNull OCServer.LifecycleState lifecycleState,
            @NotNull OCServer.Status status,
            @NotNull Type type,
            @NotNull String executable,
            int port,
            int maxPlayers,
            boolean autoStart,
            boolean autoDelete,
            int memory,
            Map<String, String> environmentVariables,
            boolean maintenance) {
        super(
                task,
                name,
                lifecycleState,
                status,
                type,
                executable,
                port,
                new ArrayList<>(),
                maxPlayers,
                autoStart,
                autoDelete,
                memory,
                environmentVariables,
                maintenance);
    }

    @Override
    public void start() {
        try {
            lifecycleState = LifecycleState.ONLINE;
            setStatus(Status.STARTING);
            Node.getInstance().getLogger().info("Starting server " + getName() + "...");

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

            String executable = getExecutable();
            String memory = getMemory() + "M";
            String[] jvmArgs = new String[0];
            String file = "server.jar";
            String[] args = AikarsFlags.generate(executable, memory, jvmArgs, file /*, "--nogui"*/);
            ProcessBuilder builder = new ProcessBuilder(args);
            builder.directory(getDirectory().toFile());
            builder.environment().putAll(getEnvironmentVariables());
            process = builder.start();
            process.onExit().thenRun(this);
            new ScreenThread().start();
        } catch (Throwable exception) {
            Node.getInstance()
                    .getLogger()
                    .log(Level.SEVERE, "Failed to start Server " + getName(), exception);
        }
    }

    @Override
    public void stop() {
        try {
            if (process != null && process.isAlive()) {
                Node.getInstance().getLogger().info("Stopping server " + getName() + "...");
                try (BufferedWriter writer = process.outputWriter()) {
                    writer.write(getType().getStopCommand() + "\n");
                    writer.flush();
                }
            }
        } catch (Throwable exception) {
            Node.getInstance()
                    .getLogger()
                    .log(Level.SEVERE, "Failed to stop Server " + getName(), exception);
        }
    }

    @Override
    public void kill() {
        try {
            if (process != null && process.isAlive()) {
                Node.getInstance().getLogger().info("Killing server " + getName() + "...");
                process.destroy();
            }
        } catch (Throwable exception) {
            Node.getInstance()
                    .getLogger()
                    .log(Level.SEVERE, "Failed to stop Server " + getName(), exception);
        }
    }

    @Override
    public @NotNull OCNode getNode() {
        return Node.getInstance().getLocalNode();
    }

    public @NotNull Path getDirectory() {
        return Path.of("servers").resolve(getName());
    }

    @Override
    public void run() {
        Node.getInstance().getLogger().info("Server " + getName() + " stopped");
        lifecycleState = LifecycleState.OFFLINE;
        setStatus(Status.OFFLINE);
    }

    public boolean isScreen() {
        return screen;
    }

    public void setScreen(boolean screen) {
        this.screen = screen;
    }

    private class ScreenThread extends Thread {
        @Override
        public void run() {
            try (BufferedReader reader = process.inputReader()) {
                while (process.isAlive()) {
                    String line = reader.readLine();
                    if (screen && line != null) {
                        Node.getInstance()
                                .getLogger()
                                .info("[%s] %s".formatted(LocalOCServer.this.getName(), line));
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    }
}
