package de.obsidiancloud.platform.local;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.packets.ServerStatusChangePacket;
import de.obsidiancloud.common.network.packets.ServerUpdatePacket;
import de.obsidiancloud.platform.PlatformObsidianCloudAPI;
import de.obsidiancloud.platform.remote.RemoteLocalOCNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public abstract class LocalOCServer extends OCServer {
    public LocalOCServer(@NotNull TransferableServerData data, @NotNull Status status) {
        super(data, status, new ArrayList<>());
    }

    @Override
    public void start() {}

    @Override
    public void kill() {
        System.exit(0);
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

    private void sendUpdatePacket(@NotNull TransferableServerData data) {
        ServerUpdatePacket packet = new ServerUpdatePacket();
        packet.setServerData(data);
        ((RemoteLocalOCNode) ObsidianCloudAPI.get().getLocalNode()).getConnection().send(packet);
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
    public @NotNull RemoteLocalOCNode getNode() {
        return (RemoteLocalOCNode) ObsidianCloudAPI.get().getLocalNode();
    }
}
