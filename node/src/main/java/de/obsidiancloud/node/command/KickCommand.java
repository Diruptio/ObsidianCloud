package de.obsidiancloud.node.command;

import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.command.Command;
import de.obsidiancloud.common.command.CommandExecutor;
import java.util.Arrays;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class KickCommand extends Command {
    public KickCommand() {
        super("kick");
        setDescription("Kick a player from the network.");
        setUsage("kick <Player> [<Reason>]");
        addAlias("disconnect");
    }

    @Override
    public void execute(@NotNull CommandExecutor executor, @NotNull String[] args) {
        if (args.length == 0) {
            executor.sendMessage("§cUsage: " + getUsage(executor));
            return;
        }

        OCPlayer player = ObsidianCloudAPI.get().getPlayer(args[0]);
        if (player == null) {
            executor.sendMessage("§cThe player §e" + args[0] + " §cis not online.");
            return;
        }

        if (args.length == 1) {
            player.kick(null);
        } else {
            String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            player.kick(LegacyComponentSerializer.legacySection().deserialize(reason));
        }
    }
}
