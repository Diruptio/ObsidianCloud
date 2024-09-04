package de.obsidiancloud.node.remote;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.network.packets.ServerStatusChangePacket;
import java.util.HashSet;
import de.obsidiancloud.common.network.packets.ServerUpdatePacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class RemoteOCServer extends OCServer {
    private final @NotNull RemoteOCNode node;

    public RemoteOCServer(
            @NotNull TransferableServerData data,
            @NotNull Status status,
            @NotNull RemoteOCNode node) {
        super(data, status, new HashSet<>());
        this.node = node;
    }

    @Override
    public void start() {
        // TODO: Send start server packet to node
    }

    @Override
    public void stop() {
        // TODO: Send stop server packet to node
    }

    @Override
    public void kill() {
        // TODO: Send kill server packet to node
    }

    private void sendUpdatePacket(@NotNull TransferableServerData data) {
        ServerUpdatePacket packet = new ServerUpdatePacket();
        packet.setServerData(data);
        node.getConnection().send(packet);
    }

    @Override
    public void setName(@NotNull String name) {
        sendUpdatePacket(
                new TransferableServerData(
                        data.task(),
                        name,
                        data.type(),
                        data.platform(),
                        data.staticServer(),
                        data.autoStart(),
                        data.executable(),
                        data.memory(),
                        data.args(),
                        data.jvmArgs(),
                        data.environmentVariables(),
                        data.port()));
    }

    @Override
    public void setAutoStart(boolean autoStart) {
        sendUpdatePacket(
                new TransferableServerData(
                        data.task(),
                        data.name(),
                        data.type(),
                        data.platform(),
                        data.staticServer(),
                        autoStart,
                        data.executable(),
                        data.memory(),
                        data.args(),
                        data.jvmArgs(),
                        data.environmentVariables(),
                        data.port()));
    }

    @Override
    public void setExecutable(@NotNull String executable) {
        sendUpdatePacket(
                new TransferableServerData(
                        data.task(),
                        data.name(),
                        data.type(),
                        data.platform(),
                        data.staticServer(),
                        data.autoStart(),
                        executable,
                        data.memory(),
                        data.args(),
                        data.jvmArgs(),
                        data.environmentVariables(),
                        data.port()));
    }

    @Override
    public void setMemory(int memory) {
        sendUpdatePacket(
                new TransferableServerData(
                        data.task(),
                        data.name(),
                        data.type(),
                        data.platform(),
                        data.staticServer(),
                        data.autoStart(),
                        data.executable(),
                        memory,
                        data.args(),
                        data.jvmArgs(),
                        data.environmentVariables(),
                        data.port()));
    }

    @Override
    public void setJvmArgs(@NotNull List<String> jvmArgs) {
        sendUpdatePacket(
                new TransferableServerData(
                        data.task(),
                        data.name(),
                        data.type(),
                        data.platform(),
                        data.staticServer(),
                        data.autoStart(),
                        data.executable(),
                        data.memory(),
                        data.args(),
                        jvmArgs,
                        data.environmentVariables(),
                        data.port()));
    }

    @Override
    public void setArgs(@NotNull List<String> args) {
        sendUpdatePacket(
                new TransferableServerData(
                        data.task(),
                        data.name(),
                        data.type(),
                        data.platform(),
                        data.staticServer(),
                        data.autoStart(),
                        data.executable(),
                        data.memory(),
                        args,
                        data.jvmArgs(),
                        data.environmentVariables(),
                        data.port()));
    }

    @Override
    public void setEnvironmentVariables(@NotNull Map<String, String> environmentVariables) {
        sendUpdatePacket(
                new TransferableServerData(
                        data.task(),
                        data.name(),
                        data.type(),
                        data.platform(),
                        data.staticServer(),
                        data.autoStart(),
                        data.executable(),
                        data.memory(),
                        data.args(),
                        data.jvmArgs(),
                        environmentVariables,
                        data.port()));
    }

    @Override
    public void setPort(int port) {
        sendUpdatePacket(
                new TransferableServerData(
                        data.task(),
                        data.name(),
                        data.type(),
                        data.platform(),
                        data.staticServer(),
                        data.autoStart(),
                        data.executable(),
                        data.memory(),
                        data.args(),
                        data.jvmArgs(),
                        data.environmentVariables(),
                        port));
    }

    @Override
    public void setStatus(@NotNull Status status) {
        ServerStatusChangePacket packet = new ServerStatusChangePacket();
        packet.setName(getName());
        packet.setStatus(status);
        node.getConnection().send(packet);
    }

    @Override
    public @NotNull RemoteOCNode getNode() {
        return node;
    }
}
