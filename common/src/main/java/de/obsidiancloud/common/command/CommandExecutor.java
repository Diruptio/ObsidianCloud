package de.obsidiancloud.common.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public interface CommandExecutor {
    /**
     * Executes the command
     *
     * @param line The command line
     */
    default void execute(@NotNull String line) {
        String[] parts = line.split(" ");
        Command command = Command.getCommand(parts[0]);
        if (command == null) {
            sendMessage("§cCommand \"" + command + "\" was not found");
        } else {
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, args.length);
            command.execute(this, args);
        }
    }

    /**
     * Sends a message to the command executor
     *
     * @param message The message to send
     */
    void sendMessage(@NotNull Component message);

    /**
     * Gets the command prefix {@code ConsoleCommand}
     *
     * @return The command prefix
     */
    String getCommandPrefix();

    /**
     * Sends a message to the command executor
     *
     * @param message The message to send
     */
    default void sendMessage(@NotNull String message) {
        sendMessage(LegacyComponentSerializer.legacySection().deserialize(message));
    }
}
