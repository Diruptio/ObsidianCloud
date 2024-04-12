package de.obsidiancloud.common.command.impl;

import de.obsidiancloud.common.command.Command;
import de.obsidiancloud.common.command.CommandExecutor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help");
        setDescription("Shows help");
        setUsage("help [command or alias]");
        addAlias("?");
    }

    @Override
    public void execute(@NotNull CommandExecutor executor, @NotNull String[] args) {
        if (args.length == 0) {
            executor.sendMessage("Help:");
            for (Command command : Command.getAllCommands()) {
                List<String> names = new ArrayList<>();
                names.add(command.getName());
                names.addAll(Arrays.asList(command.getAliases()));
                executor.sendMessage(
                        "  §b" + String.join("§7, §b", names) + " §7- " + command.getDescription());
            }
        } else {
            Command command = null;
            for (Command cmd : Command.getAllCommands()) {
                if (cmd.getName().equalsIgnoreCase(args[0])
                        || Arrays.asList(cmd.getAliases()).contains(args[0])) {
                    command = cmd;
                }
            }
            if (command == null)
                executor.sendMessage("§cCommand \"" + args[0] + "\" does not exist!");
            else {
                executor.sendMessage("§f§nHelp for " + command.getName() + ":");
                executor.sendMessage("  §7Name: §b" + command.getName());
                if (command.getAliases().length > 0)
                    executor.sendMessage(
                            "  §7Aliases: [§b"
                                    + String.join("§7, §b", command.getAliases())
                                    + "§7]");
                if (command.getUsage() != null)
                    executor.sendMessage("  §7Usage: §b" + command.getUsage());
                if (command.getDescription() != null)
                    executor.sendMessage("  §7Description: §b" + command.getDescription());
            }
        }
    }
}
