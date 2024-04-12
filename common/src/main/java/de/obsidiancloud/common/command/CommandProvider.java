package de.obsidiancloud.common.command;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CommandProvider {
    /**
     * Registers a command
     *
     * @param command The command
     */
    void registerCommand(@NotNull Command command);

    /**
     * Unregisters a command
     *
     * @param command The name of the command
     */
    void unregisterCommand(@NotNull String command);

    /**
     * Gets all registered commands
     *
     * @return Returns a {@code List<Command>} with all registered commands
     */
    @NotNull
    List<Command> getCommands();

    /**
     * Searches for a command
     *
     * @param name The name of the command.
     * @return Returns a {@code Command} if the command is registered, otherwise {@code null}.
     */
    @Nullable
    Command getCommand(@NotNull String name);
}
