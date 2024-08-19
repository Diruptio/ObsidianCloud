package de.obsidiancloud.node.command;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.command.Command;
import de.obsidiancloud.common.command.CommandExecutor;
import de.obsidiancloud.node.ObsidianCloudNode;
import de.obsidiancloud.node.local.LocalOCServer;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public class CommandCommand extends Command {
    public CommandCommand() {
        super("command");
        setDescription("Run a command on the server.");
        setUsage("command <Server> <Command>");
        addAlias("cmd");
    }

    @Override
    public void execute(@NotNull CommandExecutor executor, @NotNull String @NotNull [] args) {
        if (args.length < 2) {
            executor.sendMessage("§cUsage: " + getUsage(executor));
            return;
        }

        OCServer server = ObsidianCloudAPI.get().getServer(args[0]);
        if (!(server instanceof LocalOCServer localServer)) {
            executor.sendMessage("§cServer " + args[0] + " does not exist");
            return;
        }

        String command = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Process process = localServer.getProcess();
        try {
            if (process != null && process.isAlive()) {
                try (BufferedWriter writer = process.outputWriter()) {
                    writer.write(command + "\n");
                    writer.flush();
                }
            } else {
                ObsidianCloudNode.getLogger()
                        .warning("Cannot send command, the server is not running.");
            }
        } catch (IOException exception) {
            exception.printStackTrace(System.err);
        }
    }
}
