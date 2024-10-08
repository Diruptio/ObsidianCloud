package de.obsidiancloud.common;

import de.obsidiancloud.common.command.CommandExecutor;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represents a player on the network. */
public abstract class OCPlayer implements CommandExecutor {
    private final @NotNull UUID uuid;
    private final @NotNull String name;

    /**
     * Create a new player.
     *
     * @param uuid The uuid of the player
     * @param name The name of the player
     */
    public OCPlayer(@NotNull UUID uuid, @NotNull String name) {
        this.uuid = uuid;
        this.name = name;
    }

    /**
     * Gets the proxy of this player.
     *
     * @return Returns the proxy of this player.
     */
    public @Nullable OCServer getProxy() {
        for (OCServer server : ObsidianCloudAPI.get().getServers()) {
            if (server.getPlayers().contains(this) && server.getData().type() == OCServer.Type.PROXY) {
                return server;
            }
        }
        return null;
    }

    /**
     * Gets the server of this player.
     *
     * @return Returns the server of this player.
     */
    public @Nullable OCServer getServer() {
        for (OCServer server : ObsidianCloudAPI.get().getServers()) {
            if (server.getPlayers().contains(this) && server.getData().type() == OCServer.Type.SERVER) {
                return server;
            }
        }
        return null;
    }

    /**
     * Connects the player to the given server.
     *
     * @param server The server to connect to
     */
    public abstract void connect(@NotNull OCServer server);

    /**
     * Kicks the player from the network.
     *
     * @param message The kick reason
     */
    public abstract void kick(@Nullable Component message);

    /**
     * Gets the uuid of this player.
     *
     * @return Returns the uuid of this player.
     */
    public @NotNull UUID getUUID() {
        return uuid;
    }

    /**
     * Gets the name of this player.
     *
     * @return Returns the name of this player.
     */
    public @NotNull String getName() {
        return name;
    }
}
