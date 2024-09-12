package de.obsidiancloud.node.command;

import de.obsidiancloud.common.OCTask;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.command.Command;
import de.obsidiancloud.common.command.CommandExecutor;
import org.jetbrains.annotations.NotNull;

public class CreateCommand extends Command {
    public CreateCommand() {
        super("create");
        setDescription("Create a new server.");
        setUsage("create from <Task> [<Amount>]");
    }

    @Override
    public void execute(@NotNull CommandExecutor executor, @NotNull String[] args) {
        if (args.length >= 2 && args[0].equalsIgnoreCase("from")) {
            String taskName = args[1];
            OCTask task = ObsidianCloudAPI.get().getTask(taskName);
            if (task == null) {
                executor.sendMessage("§cThe task §e" + taskName + " §cdoes not exist.");
                return;
            }

            int amount = 1;
            if (args.length >= 3) {
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    executor.sendMessage("§cThe amount §e" + args[2] + " §cis not a number.");
                    return;
                }
            }

            for (int i = 0; i < amount; i++) {
                ObsidianCloudAPI.get()
                        .createServer(task)
                        .thenAccept(
                                server -> {
                                    if (server == null) {
                                        executor.sendMessage("§cThe server cloud not be created.");
                                    }
                                });
            }
        } else {
            executor.sendMessage("§cUsage: " + getUsage(executor));
        }
    }
}
