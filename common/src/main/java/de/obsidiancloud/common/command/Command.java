package de.obsidiancloud.common.command;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A command which can be executed by a {@link CommandExecutor} */
public abstract class Command {
    private static final List<CommandProvider> providers = new ArrayList<>();
    private final String name;
    private String description;
    private String usage;
    private String[] aliases;

    /**
     * Creates a new command
     *
     * @param name The name of the command
     */
    public Command(@NotNull String name) {
        this.name = name;
        this.aliases = new String[0];
    }

    /**
     * Executes the command
     *
     * @param executor The executor of the command
     * @param args The arguments of the command
     */
    public abstract void execute(@NotNull CommandExecutor executor, @NotNull String[] args);

    /**
     * Gets the tab completions of the command
     *
     * @param executor The executor of the command
     * @param args The arguments of the command
     * @return A {@code List<String>} with the tab completions
     */
    public @NotNull List<String> tabComplete(@NotNull CommandExecutor executor, @NotNull String[] args) {
        return new ArrayList<>();
    }

    /**
     * Gets the name of the command
     *
     * @return The name
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Gets the description of the command
     *
     * @return The description
     */
    public @Nullable String getDescription() {
        return description;
    }

    /**
     * Sets the description of the command
     *
     * @param description The description
     * @return The command
     */
    public @NotNull Command setDescription(@NotNull String description) {
        this.description = description;
        return this;
    }

    /**
     * Gets the usage of the command with the command prefix of the executor
     *
     * @param executor The executor of the command
     * @return The usage
     */
    public @Nullable String getUsage(@NotNull CommandExecutor executor) {
        return executor.getCommandPrefix() + usage;
    }

    /**
     * Sets the usage of the command
     *
     * @param usage The usage
     * @return The command
     */
    public @NotNull Command setUsage(@NotNull String usage) {
        this.usage = usage;
        return this;
    }

    /**
     * Gets the aliases of the command
     *
     * @return The aliases
     */
    public @NotNull String[] getAliases() {
        return aliases;
    }

    /**
     * Adds an alias to the command
     *
     * @param alias The alias
     * @return The command
     */
    public @NotNull Command addAlias(@NotNull String alias) {
        String[] aliases = new String[this.aliases.length + 1];
        System.arraycopy(this.aliases, 0, aliases, 0, this.aliases.length);
        aliases[this.aliases.length] = alias;
        this.aliases = aliases;
        return this;
    }

    /**
     * Gets all registered command providers
     *
     * @return A {@code List<CommandProvider>} with all registered command providers
     */
    public static @NotNull List<CommandProvider> getProviders() {
        return providers;
    }

    /**
     * Registers a command provider
     *
     * @param provider The provider to register
     * @throws IllegalArgumentException If the provider is already registered
     */
    public static void registerProvider(@NotNull CommandProvider provider) {
        if (providers.contains(provider)) {
            throw new IllegalArgumentException("Provider already registered!");
        } else {
            providers.add(provider);
        }
    }

    /**
     * Unregisters a command provider
     *
     * @param provider The provider to unregister
     * @throws IllegalArgumentException If the provider is not registered
     */
    public static void unregisterProvider(@NotNull CommandProvider provider) {
        if (providers.contains(provider)) {
            providers.remove(provider);
        } else {
            throw new IllegalArgumentException("Provider not registered!");
        }
    }

    /**
     * Gets all registered commands
     *
     * @return Returns a {@code List<Command>} with all registered commands
     */
    public static @NotNull List<Command> getAllCommands() {
        List<Command> commands = new ArrayList<>();
        for (CommandProvider provider : providers) {
            commands.addAll(provider.getCommands());
        }
        return commands;
    }

    /**
     * Gets a command by name
     *
     * @param name The name of the command
     * @return Returns the command or {@code null} if not found
     */
    public static @Nullable Command getCommand(@NotNull String name) {
        for (CommandProvider provider : providers) {
            Command command = provider.getCommand(name);
            if (command != null) {
                return command;
            }
        }
        return null;
    }
}
