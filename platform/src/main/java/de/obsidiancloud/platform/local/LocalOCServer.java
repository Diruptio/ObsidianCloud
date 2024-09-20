package de.obsidiancloud.platform.local;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.packets.ServerStatusChangePacket;
import de.obsidiancloud.common.network.packets.ServerUpdatePacket;
import de.obsidiancloud.platform.PlatformObsidianCloudAPI;
import de.obsidiancloud.platform.remote.RemoteLocalOCNode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LocalOCServer extends OCServer {
    public LocalOCServer(@NotNull TransferableServerData data, @NotNull Status status) {
        super(data, status, new HashSet<>());
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
        ((RemoteLocalOCNode) ObsidianCloudAPI.get().getLocalNode())
                .getConnection()
                .send(packet);
    }

    @Override
    public void setName(@NotNull String name) {
        sendUpdatePacket(new TransferableServerData(
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
                data.fallback()));
    }

    @Override
    public void setAutoStart(boolean autoStart) {
        sendUpdatePacket(new TransferableServerData(
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
                data.fallback()));
    }

    @Override
    public void setExecutable(@NotNull String executable) {
        sendUpdatePacket(new TransferableServerData(
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
                data.fallback()));
    }

    @Override
    public void setMemory(int memory) {
        sendUpdatePacket(new TransferableServerData(
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
                data.fallback()));
    }

    @Override
    public void setJvmArgs(@NotNull List<String> jvmArgs) {
        sendUpdatePacket(new TransferableServerData(
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
                data.fallback()));
    }

    @Override
    public void setArgs(@NotNull List<String> args) {
        sendUpdatePacket(new TransferableServerData(
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
                data.fallback()));
    }

    @Override
    public void setEnvironmentVariables(@NotNull Map<String, String> environmentVariables) {
        sendUpdatePacket(new TransferableServerData(
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
                data.fallback()));
    }

    @Override
    public void setPort(int port) {
        sendUpdatePacket(new TransferableServerData(
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
                data.fallback()));
    }

    @Override
    public void setLinkToProxies(@Nullable List<String> linkToProxies) {
        sendUpdatePacket(new TransferableServerData(
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
                data.port(),
                linkToProxies,
                data.fallback()));
    }

    @Override
    public void setFallback(boolean fallback) {
        sendUpdatePacket(new TransferableServerData(
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
                data.port(),
                data.linkToProxies(),
                fallback));
    }

    @Override
    public @NotNull RemoteLocalOCNode getNode() {
        return (RemoteLocalOCNode) ObsidianCloudAPI.get().getLocalNode();
    }
}
