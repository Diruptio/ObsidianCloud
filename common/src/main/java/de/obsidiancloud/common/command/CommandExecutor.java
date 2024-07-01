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
    void execute(@NotNull String line);

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
