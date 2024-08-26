package de.obsidiancloud.node.remote;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.network.packets.ServerStatusChangePacket;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public class RemoteOCServer extends OCServer {
    private final @NotNull RemoteOCNode node;

    public RemoteOCServer(
            @NotNull TransferableServerData data,
            @NotNull Status status,
            @NotNull RemoteOCNode node) {
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
        node.getConnection().send(packet);
    }

    @Override
    public @NotNull RemoteOCNode getNode() {
        return node;
    }
}
