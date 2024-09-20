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
            String taskArg = args[1];
            OCTask task = ObsidianCloudAPI.get().getTask(taskArg);
            if (task == null) {
                executor.sendMessage("§cThe task §e" + taskArg + " §cdoes not exist.");
                return;
            }

            int amount = 1;
            if (args.length >= 3) {
                String amountArg = args[2];
                try {
                    amount = Integer.parseInt(amountArg);
                } catch (NumberFormatException e) {
                    executor.sendMessage("§cThe amount §e" + amountArg + " §cis not a number.");
                    return;
                }
            }

            for (int i = 0; i < amount; i++) {
                ObsidianCloudAPI.get().createServer(task).thenAccept(server -> {
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
