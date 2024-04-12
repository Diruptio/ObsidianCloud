package de.obsidiancloud.node.command;

import de.obsidiancloud.common.command.Command;
import de.obsidiancloud.common.command.CommandExecutor;
import de.obsidiancloud.node.Node;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class ShutdownCommand extends Command {
    private final Map<CommandExecutor, Long> lastUsed = new HashMap<>();

    public ShutdownCommand() {
        super("shutdown");
        setDescription("Shuts down the node");
        setUsage("shutdown");
        addAlias("stop");
        addAlias("exit");
    }

    @Override
    public void execute(@NotNull CommandExecutor executor, @NotNull String[] args) {
        if (lastUsed.getOrDefault(executor, 0L) + 10000 > System.currentTimeMillis()) {
            lastUsed.remove(executor);
            executor.sendMessage("§cShutting down...");
            Node.getInstance().shutdown();
        } else {
            lastUsed.put(executor, System.currentTimeMillis());
            executor.sendMessage(
                    "§cAre you sure? Type the command again in the next §e10 seconds §cto confirm.");
        }
    }
}
