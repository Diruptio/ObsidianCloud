package de.obsidiancloud.node.console;

import de.obsidiancloud.common.command.CommandExecutor;
import de.obsidiancloud.node.command.Command;
import java.util.Arrays;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class ConsoleCommandExecutor implements CommandExecutor {
    private final @NotNull Logger logger;

    /**
     * Creates a new console command executor.
     *
     * @param logger The main logger.
     */
    public ConsoleCommandExecutor(@NotNull Logger logger) {
        this.logger = logger;
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        logger.info(ANSIComponentSerializer.ansi().serialize(message));
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
    public String getCommandPrefix() {
        return "";
    }
}
