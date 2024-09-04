package de.obsidiancloud.platform.remote;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.packets.ServerStatusChangePacket;
import de.obsidiancloud.common.network.packets.ServerUpdatePacket;
import de.obsidiancloud.platform.PlatformObsidianCloudAPI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class RemoteOCServer extends OCServer {
    private final @NotNull OCNode node;

    public RemoteOCServer(
            @NotNull TransferableServerData data, @NotNull Status status, @NotNull OCNode node) {
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
        ((RemoteLocalOCNode) ObsidianCloudAPI.get().getLocalNode()).getConnection().send(packet);
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
        ((PlatformObsidianCloudAPI) ObsidianCloudAPI.get())
                .getLocalNode()
                .getConnection()
                .send(packet);
    }

    @Override
    public @NotNull OCNode getNode() {
        return node;
    }
}
