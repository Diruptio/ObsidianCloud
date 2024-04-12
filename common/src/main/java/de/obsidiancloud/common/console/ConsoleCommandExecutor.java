package de.obsidiancloud.common.console;

import de.obsidiancloud.common.command.CommandExecutor;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class ConsoleCommandExecutor implements CommandExecutor {
    private Logger logger;

    /**
     * Creates a new console command executor.
     *
     * @param logger The main logger.
     */
    public ConsoleCommandExecutor(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        logger.info(ANSIComponentSerializer.ansi().serialize(message));
    }

    @Override
    public String getCommandPrefix() {
        return "";
    }
}
