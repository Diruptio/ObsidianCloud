package de.obsidiancloud.node.local;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.command.CommandExecutor;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.packets.ServerPortChangedPacket;
import de.obsidiancloud.common.network.packets.ServerStatusChangedPacket;
import de.obsidiancloud.common.network.packets.ServerUpdatedPacket;
import de.obsidiancloud.node.ObsidianCloudNode;
import de.obsidiancloud.node.util.Flags;
import de.obsidiancloud.node.util.NetworkUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocalOCServer extends OCServer {
    private final Set<CommandExecutor> screenReaders = new HashSet<>();
    private Connection connection;
    private Process process;

    public LocalOCServer(@NotNull TransferableServerData data, @NotNull Status status) {
        super(data, status, new HashSet<>());
    }

    @Override
    public void start() {
        try {
            setStatus(Status.STARTING);
            ObsidianCloudNode.getLogger().info("Starting server " + getName() + "...");

            port = NetworkUtil.getFreePort(getData().port());
            NetworkUtil.blockPort(port);
            ServerPortChangedPacket packet = new ServerPortChangedPacket();
            packet.setName(getName());
            packet.setPort(port);
            for (Connection connection : ObsidianCloudNode.getNetworkServer().getConnections()) {
                connection.send(packet);
            }

            List<String> command = new ArrayList<>();
            command.add(getData().executable());
            if (getData().type() != Type.CUSTOM) {
                command.add("-Xms" + getData().memory() + "M");
                command.add("-Xmx" + getData().memory() + "M");
                command.addAll(getData().jvmArgs());
                command.add("-jar");
                command.add("server.jar");
                if (getData().platform() == Platform.PAPER || getData().platform() == Platform.FABRIC) {
                    String serverProperties = Files.readString(getDirectory().resolve("server.properties"));
                    serverProperties = serverProperties.replaceAll("server-port=.*", "server-port=" + port);
                    Files.writeString(getDirectory().resolve("server.properties"), serverProperties);
                } else if (getData().platform() == Platform.VELOCITY) {
                    command.add("--port");
                    command.add(String.valueOf(port));
                }
                command.addAll(getData().args());
            }
            for (int i = 0; i < command.size(); i++) {
                String arg = command.get(i);
                if (arg.equals("%AIKARS_FLAGS%")) {
                    command.remove(i);
                    command.addAll(i, List.of(Flags.AIKARS_FLAGS));
                    i += Flags.AIKARS_FLAGS.length - 1;
                } else if (arg.equals("%VELOCITY_FLAGS%")) {
                    command.remove(i);
                    command.addAll(i, List.of(Flags.VELOCITY_FLAGS));
                    i += Flags.VELOCITY_FLAGS.length - 1;
                } else {
                    command.set(i, arg.replace("%SERVER_PORT%", String.valueOf(port)));
                }
            }
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(getDirectory().toFile());
            builder.environment().putAll(getData().environmentVariables());
            builder.environment().put("OC_NODE_NAME", getNode().getName());
            builder.environment().put("OC_NODE_HOST", "127.0.0.1");
            int nodePort = ObsidianCloudNode.getNetworkServer().getPort();
            builder.environment().put("OC_NODE_PORT", String.valueOf(nodePort));
            builder.environment()
                    .put("OC_CLUSTERKEY", ObsidianCloudNode.getClusterKey().get());
            builder.environment().put("OC_SERVER_DATA", getData().toString());
            builder.environment().put("OC_SERVER_PORT", String.valueOf(port));
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

    private void sendUpdatedPacket() {
        ServerUpdatedPacket packet = new ServerUpdatedPacket();
        packet.setServerData(data);
        for (Connection connection : ObsidianCloudNode.getNetworkServer().getConnections()) {
            connection.send(packet);
        }
    }

    @Override
    public void setName(@NotNull String name) {
        data = new TransferableServerData(
                data.task(),
                name,
                data.type(),
                data.platform(),
                data.staticServer(),
                data.autoStart(),
                data.executable(),
                data.memory(),
                data.jvmArgs(),
                data.args(),
                data.environmentVariables(),
                data.port(),
                data.linkToProxies(),
                data.fallback());
        sendUpdatedPacket();
    }

    @Override
    public void setAutoStart(boolean autoStart) {
        data = new TransferableServerData(
                data.task(),
                data.name(),
                data.type(),
                data.platform(),
                data.staticServer(),
                autoStart,
                data.executable(),
                data.memory(),
                data.jvmArgs(),
                data.args(),
                data.environmentVariables(),
                data.port(),
                data.linkToProxies(),
                data.fallback());
        sendUpdatedPacket();
    }

    @Override
    public void setExecutable(@NotNull String executable) {
        data = new TransferableServerData(
                data.task(),
                data.name(),
                data.type(),
                data.platform(),
                data.staticServer(),
                data.autoStart(),
                executable,
                data.memory(),
                data.jvmArgs(),
                data.args(),
                data.environmentVariables(),
                data.port(),
                data.linkToProxies(),
                data.fallback());
        sendUpdatedPacket();
    }

    @Override
    public void setMemory(int memory) {
        data = new TransferableServerData(
                data.task(),
                data.name(),
                data.type(),
                data.platform(),
                data.staticServer(),
                data.autoStart(),
                data.executable(),
                memory,
                data.jvmArgs(),
                data.args(),
                data.environmentVariables(),
                data.port(),
                data.linkToProxies(),
                data.fallback());
        sendUpdatedPacket();
    }

    @Override
    public void setJvmArgs(@NotNull List<String> jvmArgs) {
        data = new TransferableServerData(
                data.task(),
                data.name(),
                data.type(),
                data.platform(),
                data.staticServer(),
                data.autoStart(),
                data.executable(),
                data.memory(),
                jvmArgs,
                data.args(),
                data.environmentVariables(),
                data.port(),
                data.linkToProxies(),
                data.fallback());
        sendUpdatedPacket();
    }

    @Override
    public void setArgs(@NotNull List<String> args) {
        data = new TransferableServerData(
                data.task(),
                data.name(),
                data.type(),
                data.platform(),
                data.staticServer(),
                data.autoStart(),
                data.executable(),
                data.memory(),
                data.jvmArgs(),
                args,
                data.environmentVariables(),
                data.port(),
                data.linkToProxies(),
                data.fallback());
        sendUpdatedPacket();
    }

    @Override
    public void setEnvironmentVariables(@NotNull Map<String, String> environmentVariables) {
        data = new TransferableServerData(
                data.task(),
                data.name(),
                data.type(),
                data.platform(),
                data.staticServer(),
                data.autoStart(),
                data.executable(),
                data.memory(),
                data.jvmArgs(),
                data.args(),
                environmentVariables,
                data.port(),
                data.linkToProxies(),
                data.fallback());
        sendUpdatedPacket();
    }

    @Override
    public void setPort(int port) {
        data = new TransferableServerData(
                data.task(),
                data.name(),
                data.type(),
                data.platform(),
                data.staticServer(),
                data.autoStart(),
                data.executable(),
                data.memory(),
                data.jvmArgs(),
                data.args(),
                data.environmentVariables(),
                port,
                data.linkToProxies(),
                data.fallback());
        sendUpdatedPacket();
    }

    @Override
    public void setLinkToProxies(@Nullable List<String> linkToProxies) {
        data = new TransferableServerData(
                data.task(),
                data.name(),
                data.type(),
                data.platform(),
                data.staticServer(),
                data.autoStart(),
                data.executable(),
                data.memory(),
                data.jvmArgs(),
                data.args(),
                data.environmentVariables(),
                data.port(),
                linkToProxies,
                data.fallback());
        sendUpdatedPacket();
    }

    @Override
    public void setFallback(boolean fallback) {
        data = new TransferableServerData(
                data.task(),
                data.name(),
                data.type(),
                data.platform(),
                data.staticServer(),
                data.autoStart(),
                data.executable(),
                data.memory(),
                data.jvmArgs(),
                data.args(),
                data.environmentVariables(),
                data.port(),
                data.linkToProxies(),
                fallback);
        sendUpdatedPacket();
    }

    @Override
    public @NotNull OCNode getNode() {
        return ObsidianCloudAPI.get().getLocalNode();
    }

    public @NotNull Path getDirectory() {
        return Path.of("servers").resolve(getName());
    }

    public @Nullable Connection getConnection() {
        return connection;
    }

    public void setConnection(@NotNull Connection connection) {
        this.connection = connection;
    }

    private void stopped() {
        ObsidianCloudNode.getLogger().info("Server " + getName() + " stopped");
        NetworkUtil.unblockPort(port);
        ServerPortChangedPacket packet = new ServerPortChangedPacket();
        packet.setName(getName());
        packet.setPort(-1);
        for (Connection connection : ObsidianCloudNode.getNetworkServer().getConnections()) {
            connection.send(packet);
        }
        setStatus(Status.OFFLINE);
    }

    public @NotNull Set<CommandExecutor> getScreenReaders() {
        return screenReaders;
    }

    public @Nullable Process getProcess() {
        return process;
    }

    private class ScreenThread extends Thread {
        public ScreenThread() {
            setName("ScreenThread");
            setDaemon(true);
        }

        @Override
        public void run() {
            try (BufferedReader reader = Objects.requireNonNull(process).inputReader()) {
                while (process.isAlive()) {
                    String line = reader.readLine();
                    for (CommandExecutor screenReader : screenReaders) {
                        screenReader.sendMessage(line);
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    }
}
