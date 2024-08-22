package de.obsidiancloud.platform.remote;

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
    public RemoteOCPlayer(@NotNull UUID uuid, @NotNull String name) {
        super(uuid, name);
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
