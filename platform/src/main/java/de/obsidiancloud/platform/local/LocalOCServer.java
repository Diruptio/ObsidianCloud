package de.obsidiancloud.platform.local;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.packets.ServerUpdatePacket;
import de.obsidiancloud.platform.PlatformObsidianCloudAPI;
import de.obsidiancloud.platform.remote.RemoteLocalOCNode;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;

public abstract class LocalOCServer extends OCServer {
    public LocalOCServer(@NotNull OCServer.TransferableServerData data) {
        super(data, new HashSet<>());
    }

    @Override
    public void start() {}

    @Override
    public void kill() {
        System.exit(0);
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
        ServerUpdatePacket packet = new ServerUpdatePacket();
        packet.setServerData(data);
        ((PlatformObsidianCloudAPI) ObsidianCloudAPI.get())
                .getLocalNode()
                .getConnection()
                .send(packet);
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
        ((PlatformObsidianCloudAPI) ObsidianCloudAPI.get())
                .getLocalNode()
                .getConnection()
                .send(packet);
    }

    @Override
    public @NotNull RemoteLocalOCNode getNode() {
        return (RemoteLocalOCNode) ObsidianCloudAPI.get().getLocalNode();
    }
}
