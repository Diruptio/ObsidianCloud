package de.obsidiancloud.node.command;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.command.Command;
import de.obsidiancloud.common.command.CommandExecutor;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class ServerCommand extends Command {

    public ServerCommand() {
        super("server");
        setUsage("server <server>");
    }

    @Override
    public void execute(@NotNull final CommandExecutor executor, final @NotNull String[] args) {
        final int argCount = args.length;

        if (argCount == 0) {
            executor.sendMessage(
                    "Servers: "
                            + ObsidianCloudAPI.get().getServers().stream()
                                    .map(OCServer::getName)
                                    .collect(Collectors.joining(", ")));

            return;
        }

        final String serverArg = args[0];
        final OCServer server = ObsidianCloudAPI.get().getServer(serverArg);

        if (server == null) {
            executor.sendMessage("§cServer with name §e" + serverArg + " could not be found.");

            return;
        }

        if (argCount == 1) {
            executor.sendMessage("server " + serverArg + " <start|stop|restart|kill|delete|set>");

            return;
        }

        final OCServer.TransferableServerData data = server.getData();

        switch (args[1]) {
            case "start" -> {
                if (data.status() != OCServer.Status.OFFLINE) {
                    executor.sendMessage("§cServer is already running.");

                    return;
                }

                server.start();
            }

            case "stop" -> {
                if (data.status() == OCServer.Status.OFFLINE) {
                    executor.sendMessage("§cServer is not running.");
                } else if (data.status() == OCServer.Status.STARTING) {
                    executor.sendMessage("§cServer is currently starting.");
                } else {
                    server.stop();
                }
            }

            case "restart" -> {
                if (data.status() == OCServer.Status.OFFLINE) {
                    executor.sendMessage("§cServer is not running.");
                } else {
                    server.stop();
                    server.start();
                }
            }

            case "kill" -> {
                if (data.status() == OCServer.Status.OFFLINE) {
                    executor.sendMessage("§cServer is not running.");
                } else {
                    server.kill();
                }
            }

            case "delete" -> {
                if (data.status() == OCServer.Status.STARTING) {
                    executor.sendMessage("§cServer is currently starting.");
                } else if (data.status() != OCServer.Status.OFFLINE) {
                    executor.sendMessage("§cServer is currently running.");
                } else {
                    ObsidianCloudAPI.get().deleteServer(server);
                }
            }

            case "set" -> {
                if (argCount == 2) {
                    executor.sendMessage(
                            "server " + serverArg + " set <name|autostart|memory|env>");

                    return;
                }

                switch (args[2]) {
                    case "name" -> {
                        if (argCount == 3) {
                            executor.sendMessage("server " + serverArg + " set name <name>");

                            return;
                        }

                        // TODO Implement set name args[3]
                    }

                    case "autostart" -> {
                        if (argCount == 3) {
                            executor.sendMessage(
                                    "server " + serverArg + " set autostart <true|false>");

                            return;
                        }

                        // TODO Implement set autostart args[3]
                    }

                    case "memory" -> {
                        if (argCount == 3) {
                            executor.sendMessage(
                                    "server " + serverArg + " set memory <memory in mb>");

                            return;
                        }

                        // TODO Implement set memory args[3]
                    }

                    case "env" -> {
                        if (argCount == 3) {
                            executor.sendMessage("server " + serverArg + " set env <set|remove>");

                            return;
                        }

                        switch (args[3]) {
                            case "set" -> {
                                if (argCount == 4) {
                                    executor.sendMessage(
                                            "server " + serverArg + " set env set <key>");

                                    return;
                                }

                                final String keyArg = args[4];

                                if (argCount == 5) {
                                    executor.sendMessage(
                                            "server "
                                                    + serverArg
                                                    + " set env set "
                                                    + keyArg
                                                    + " <value>");

                                    return;
                                }

                                // TODO Implement set env args[5]
                            }

                            case "remove" -> {
                                if (argCount == 4) {
                                    executor.sendMessage(
                                            "server " + serverArg + " set env remove <key>");

                                    return;
                                }

                                // TODO Implement remove env args[4]
                            }

                            default ->
                                    executor.sendMessage(
                                            "server " + serverArg + " set env <set|remove>");
                        }
                    }

                    default ->
                            executor.sendMessage(
                                    "server " + serverArg + " set <name|autostart|memory|env>");
                }
            }

            default ->
                    executor.sendMessage(
                            "server " + serverArg + " <start|stop|restart|kill|delete|set>");
        }
    }
}
