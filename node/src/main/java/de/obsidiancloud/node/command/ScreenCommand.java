package de.obsidiancloud.node.command;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.command.Command;
import de.obsidiancloud.common.command.CommandExecutor;
import de.obsidiancloud.node.local.LocalOCServer;
import org.jetbrains.annotations.NotNull;

public class ScreenCommand extends Command {
    public ScreenCommand() {
        super("screen");
        setDescription("Enables/disables screen mirroring of a server.");
        setUsage("screen <Server>");
        addAlias("scr");
    }

    @Override
    public void execute(@NotNull CommandExecutor executor, @NotNull String[] args) {
        if (args.length == 0) {
            executor.sendMessage("§cUsage: " + getUsage(executor));
            return;
        }

        OCServer server = ObsidianCloudAPI.get().getServer(args[0]);
        if (server == null) {
            executor.sendMessage("§cThe server §e" + args[0] + " §cdoes not exist.");
            return;
        }

        if (server instanceof LocalOCServer localServer) {
            if (localServer.isScreen()) {
                localServer.setScreen(false);
                executor.sendMessage("§aScreen mirroring of §e" + args[0] + " §ahas been disabled.");
            } else {
                localServer.setScreen(true);
                executor.sendMessage("§aScreen mirroring of §e" + args[0] + " §ahas been enabled.");
            }
        } else {
            executor.sendMessage("§cThe server §e" + args[0] + " §cis not a local server.");
        }
    }
}
