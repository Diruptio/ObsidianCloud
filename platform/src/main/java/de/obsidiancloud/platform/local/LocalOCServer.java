package de.obsidiancloud.platform.local;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.packets.ServerStatusChangePacket;
import de.obsidiancloud.platform.PlatformObsidianCloudAPI;
import de.obsidiancloud.platform.remote.RemoteLocalOCNode;
import java.util.HashSet;
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

    @Override
    public @NotNull RemoteLocalOCNode getNode() {
        return (RemoteLocalOCNode) ObsidianCloudAPI.get().getLocalNode();
    }
}
