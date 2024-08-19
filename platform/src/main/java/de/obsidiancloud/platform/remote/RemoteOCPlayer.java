package de.obsidiancloud.platform.remote;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.network.packets.PlayerKickPacket;
import de.obsidiancloud.common.network.packets.PlayerMessagePacket;
import de.obsidiancloud.platform.PlatformObsidianCloudAPI;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A player on a remote server. */
public class RemoteOCPlayer extends OCPlayer {
    private final @NotNull OCNode node;

    public RemoteOCPlayer(@NotNull UUID uuid, @NotNull String name, @NotNull OCNode node) {
        super(uuid, name);
        this.node = node;
    }

    @Override
    public @Nullable RemoteOCServer getProxy() {
        if (node.isConnected()) {
            for (OCServer server : node.getServers()) {
                if (server.getData().type().isProxy() && server.getPlayers().contains(this)) {
                    return (RemoteOCServer) server;
                }
            }
        }
        return null;
    }

    @Override
    public @Nullable RemoteOCServer getServer() {
        if (node.isConnected()) {
            for (OCServer server : node.getServers()) {
                if (!server.getData().type().isProxy() && server.getPlayers().contains(this)) {
                    return (RemoteOCServer) server;
                }
            }
        }
        return null;
    }

    @Override
    public void connect(@NotNull OCServer server) {
        // TODO: Send packet to getProxy().getNode() to connect player to server
    }

    @Override
    public void kick(@Nullable Component message) {
        PlatformObsidianCloudAPI api = (PlatformObsidianCloudAPI) ObsidianCloudAPI.get();
        PlayerKickPacket packet = new PlayerKickPacket();
        packet.setUUID(getUUID());
        packet.setMessage(message);
        OCServer proxy = getProxy();
        OCServer server = proxy != null ? proxy : getServer();
        if (server != null) {
            api.getLocalNode().getConnection().send(packet);
        }
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        PlatformObsidianCloudAPI api = (PlatformObsidianCloudAPI) ObsidianCloudAPI.get();
        PlayerMessagePacket packet = new PlayerMessagePacket();
        packet.setUUID(getUUID());
        packet.setMessage(message);
        OCServer proxy = getProxy();
        OCServer server = proxy != null ? proxy : getServer();
        if (server != null) {
            api.getLocalNode().getConnection().send(packet);
        }
    }

    @Override
    public String getCommandPrefix() {
        return "/cloud ";
    }
}
