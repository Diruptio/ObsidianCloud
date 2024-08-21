package de.obsidiancloud.platform.remote;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.packets.ServerUpdatePacket;
import de.obsidiancloud.platform.PlatformObsidianCloudAPI;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public class RemoteOCServer extends OCServer {
    private final @NotNull OCNode node;

    public RemoteOCServer(@NotNull OCServer.TransferableServerData data, @NotNull OCNode node) {
        super(data, new ArrayList<>());
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
    public @NotNull OCNode getNode() {
        return node;
    }
}
