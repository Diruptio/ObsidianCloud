package de.obsidiancloud.node.command;

import de.obsidiancloud.common.command.CommandExecutor;
import de.obsidiancloud.node.ObsidianCloudNode;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends Command {
    public ReloadCommand() {
        super("reload");
        setDescription("Reloads ObsidianCloud (or just the tasks)");
        setUsage("reload [tasks]");
        addAlias("rl");
    }

    @Override
    public void execute(@NotNull CommandExecutor executor, @NotNull String[] args) {
        if (args.length == 0) {
            executor.sendMessage("§eReloading ObsidianCloud...");
            ObsidianCloudNode.reload();
        } else if (args[0].equalsIgnoreCase("tasks")) {
            executor.sendMessage("§eReloading tasks...");
            ObsidianCloudNode.reloadTasks();
        }
    }
}
