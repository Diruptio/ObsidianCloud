package de.obsidiancloud.node.remote;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.node.command.Command;
import java.util.Arrays;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RemoteOCPlayer extends OCPlayer {
    private final @NotNull OCNode node;

    public RemoteOCPlayer(@NotNull UUID uuid, @NotNull String name, @NotNull OCNode node) {
        super(uuid, name);
        this.node = node;
    }

    @Override
    public @Nullable OCServer getProxy() {
        if (node.isConnected()) {
            for (OCServer server : node.getServers()) {
                if (server.getType().isProxy() && server.getPlayers().contains(this)) {
                    return server;
                }
            }
        }
        return null;
    }

    @Override
    public @Nullable OCServer getServer() {
        if (node.isConnected()) {
            for (OCServer server : node.getServers()) {
                if (!server.getType().isProxy() && server.getPlayers().contains(this)) {
                    return server;
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
    public void disconnect(@Nullable Component message) {
        // TODO: Send packet to getProxy().getNode() to disconnect player
    }

    @Override
    public void execute(@NotNull String line) {
        String[] parts = line.split(" ");
        Command command = Command.getCommand(parts[0]);
        if (command == null) {
            sendMessage("§cCommand \"" + command + "\" was not found");
        } else {
            command.execute(this, Arrays.copyOfRange(parts, 1, parts.length));
        }
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        // TODO: Send packet to getProxy().getNode() to send message to player
    }

    @Override
    public String getCommandPrefix() {
        return "/cloud ";
    }
}
