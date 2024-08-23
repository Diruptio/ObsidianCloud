package de.obsidiancloud.platform.remote;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.packets.ServerStatusChangePacket;
import de.obsidiancloud.platform.PlatformObsidianCloudAPI;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public class RemoteOCServer extends OCServer {
    private final @NotNull OCNode node;

    public RemoteOCServer(
            @NotNull TransferableServerData data, @NotNull Status status, @NotNull OCNode node) {
        super(data, status, new ArrayList<>());
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
