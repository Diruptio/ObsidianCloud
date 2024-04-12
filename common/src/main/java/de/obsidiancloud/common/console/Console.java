package de.obsidiancloud.common.console;

import de.obsidiancloud.common.command.Command;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Console implements Runnable {
    private final Logger logger;
    private final ConsoleCommandExecutor executor;
    private final Terminal terminal;
    private final LineReader reader;
    private final Thread thread;
    private boolean running = true;

    public Console(Logger logger, ConsoleCommandExecutor executor) throws IOException {
        this.logger = logger;
        this.executor = executor;
        try (Terminal terminal =
                TerminalBuilder.builder()
                        .color(true)
                        .system(true)
                        .encoding(StandardCharsets.UTF_8)
                        .build()) {
            this.terminal = terminal;
        }
        reader =
                LineReaderBuilder.builder()
                        .terminal(terminal)
                        .completer(new CommandCompleter())
                        .parser(new DefaultParser())
                        .history(new DefaultHistory())
                        .build();
        reader.setAutosuggestion(LineReader.SuggestionType.COMPLETER);
        this.thread = new Thread(this);
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        try {
            running = false;
            thread.interrupt();
            terminal.close();
        } catch (IOException exception) {
            if (!(exception instanceof InterruptedIOException)) {
                logger.log(Level.SEVERE, "Failed to close terminal", exception);
            }
        }
    }

    @Override
    public void run() {
        try {
            String line;
            while (running && (line = reader.readLine("> ")) != null) {
                String[] parts = line.split(" ");
                if (parts.length > 0) {
                    Command command = null;
                    for (Command cmd : Command.getAllCommands()) {
                        if (cmd.getName().equalsIgnoreCase(parts[0])
                                || Arrays.asList(cmd.getAliases()).contains(parts[0])) {
                            command = cmd;
                        }
                    }
                    if (command != null) {
                        command.execute(executor, Arrays.copyOfRange(parts, 1, parts.length));
                    } else {
                        executor.sendMessage("§cCommand not found!");
                    }
                }
            }
        } catch (UserInterruptException | EndOfFileException ignored) {
            if (running) {
                executor.execute("shutdown");
                run();
            }
        }
    }
}
