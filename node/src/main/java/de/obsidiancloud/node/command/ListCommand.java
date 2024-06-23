package de.obsidiancloud.node.command;

import de.obsidiancloud.common.OCPlayer;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.command.Command;
import de.obsidiancloud.common.command.CommandExecutor;
import org.jetbrains.annotations.NotNull;

public class ListCommand extends Command {
    public ListCommand() {
        super("list");
        setDescription("Lists all online players.");
        setUsage("list");
    }

    @Override
    public void execute(@NotNull CommandExecutor executor, @NotNull String[] args) {
        executor.sendMessage("§7Online players:");
        for (OCPlayer player : ObsidianCloudAPI.get().getPlayers()) {
            String str = "§7- " + player.getName();
            OCServer proxy = player.getProxy();
            OCServer server = player.getServer();
            if (proxy != null || server != null) {
                str += " §7(";
            }
            if (proxy != null) {
                str += proxy.getName();
            }
            if (proxy != null && server != null) {
                str += " -> ";
            }
            if (server != null) {
                str += server.getName();
            }
            if (proxy != null || server != null) {
                str += ")";
            }
            executor.sendMessage(str);
        }
    }
}
