package de.obsidiancloud.node.command;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.command.Command;
import de.obsidiancloud.common.command.CommandExecutor;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class ServerCommand extends Command {

    public ServerCommand() {
        super("server");
        setUsage("server <server>");
        addAlias("ser");
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
            executor.sendMessage("§cServer with name §e" + serverArg + " §ccould not be found.");

            return;
        }

        if (argCount == 1) {
            executor.sendMessage(
                    "§cUsage: "
                            + executor.getCommandPrefix()
                            + "server "
                            + serverArg
                            + " <start|stop|restart|kill|delete|set>");

            return;
        }

        final OCServer.Status status = server.getStatus();

        switch (args[1]) {
            case "start" -> {
                if (status != OCServer.Status.OFFLINE) {
                    executor.sendMessage("§cServer is already running.");

                    return;
                }

                server.start();
            }

            case "stop" -> {
                if (status == OCServer.Status.OFFLINE) {
                    executor.sendMessage("§cServer is not running.");
                } else if (status == OCServer.Status.STARTING) {
                    executor.sendMessage("§cServer is currently starting.");
                } else {
                    server.stop();
                }
            }

            case "restart" -> {
                if (status == OCServer.Status.OFFLINE) {
                    executor.sendMessage("§cServer is not running.");
                } else {
                    server.stop();
                    server.start();
                }
            }

            case "kill" -> {
                if (status == OCServer.Status.OFFLINE) {
                    executor.sendMessage("§cServer is not running.");
                } else {
                    server.kill();
                }
            }

            case "delete" -> {
                if (status == OCServer.Status.STARTING) {
                    executor.sendMessage("§cServer is currently starting.");
                } else if (status != OCServer.Status.OFFLINE) {
                    executor.sendMessage("§cServer is currently running.");
                } else {
                    ObsidianCloudAPI.get().deleteServer(server);
                }
            }

            case "set" -> {
                if (argCount == 2) {
                    executor.sendMessage(
                            "§cUsage: "
                                    + executor.getCommandPrefix()
                                    + "server "
                                    + serverArg
                                    + " set <name|autostart|memory|env>");

                    return;
                }

                switch (args[2]) {
                    case "name" -> {
                        if (argCount == 3) {
                            executor.sendMessage(
                                    "§cUsage: "
                                            + executor.getCommandPrefix()
                                            + "server "
                                            + serverArg
                                            + " set name <name>");

                            return;
                        }

                        final String value = args[3];

                        if (ObsidianCloudAPI.get().getServer(value) != null) {
                            executor.sendMessage("§cName already exists");

                            return;
                        }

                        server.setName(value);
                    }

                    case "autostart" -> {
                        if (argCount == 3) {
                            executor.sendMessage(
                                    "§cUsage: "
                                            + executor.getCommandPrefix()
                                            + "server "
                                            + serverArg
                                            + " set autostart <true|false>");

                            return;
                        }

                        final String value = args[3];

                        final boolean condition = value.equalsIgnoreCase("true");

                        if (!condition && !value.equalsIgnoreCase("false")) {
                            executor.sendMessage(
                                    "§cUsage: "
                                            + executor.getCommandPrefix()
                                            + "server "
                                            + serverArg
                                            + " set autostart <true|false>");

                            return;
                        }

                        server.setAutoStart(condition);
                    }

                    case "memory" -> {
                        if (argCount == 3) {
                            executor.sendMessage(
                                    "§cUsage: "
                                            + executor.getCommandPrefix()
                                            + "server "
                                            + serverArg
                                            + " set memory <memory in mb>");

                            return;
                        }

                        try {
                            final int value = Integer.parseInt(args[3]);

                            if (value <= 0) {
                                executor.sendMessage("§cMemory should be more than 0");

                                return;
                            }

                            server.setMemory(value);
                        } catch (final NumberFormatException exception) {
                            executor.sendMessage(
                                    "§cUsage: "
                                            + executor.getCommandPrefix()
                                            + "server "
                                            + serverArg
                                            + " set memory <memory in mb>");
                        }
                    }

                    case "env" -> {
                        if (argCount == 3) {
                            executor.sendMessage(
                                    "§cUsage: "
                                            + executor.getCommandPrefix()
                                            + "server "
                                            + serverArg
                                            + " set env <set|remove>");

                            return;
                        }

                        switch (args[3]) {
                            case "set" -> {
                                if (argCount == 4) {
                                    executor.sendMessage(
                                            "§cUsage: "
                                                    + executor.getCommandPrefix()
                                                    + "server "
                                                    + serverArg
                                                    + " set env set <key>");

                                    return;
                                }

                                final String keyArg = args[4];

                                if (argCount == 5) {
                                    executor.sendMessage(
                                            "§cUsage: "
                                                    + executor.getCommandPrefix()
                                                    + "server "
                                                    + serverArg
                                                    + " set env set "
                                                    + keyArg
                                                    + " <value>");

                                    return;
                                }

                                final Map<String, String> newEnvironmentVariables =
                                        new HashMap<>(server.getData().environmentVariables());

                                newEnvironmentVariables.put(keyArg, args[5]);

                                server.setEnvironmentVariables(newEnvironmentVariables);
                            }

                            case "remove" -> {
                                if (argCount == 4) {
                                    executor.sendMessage(
                                            "§cUsage: "
                                                    + executor.getCommandPrefix()
                                                    + "server "
                                                    + serverArg
                                                    + " set env remove <key>");

                                    return;
                                }

                                final String keyArg = args[4];

                                final Map<String, String> oldEnvironmentVariables =
                                        server.getData().environmentVariables();

                                if (!oldEnvironmentVariables.containsKey(keyArg)) {
                                    executor.sendMessage("§cKey not found");

                                    return;
                                }

                                final Map<String, String> newEnvironmentVariables =
                                        new HashMap<>(server.getData().environmentVariables());

                                newEnvironmentVariables.remove(args[4]);

                                server.setEnvironmentVariables(newEnvironmentVariables);
                            }

                            default ->
                                    executor.sendMessage(
                                            "§cUsage: "
                                                    + executor.getCommandPrefix()
                                                    + "server "
                                                    + serverArg
                                                    + " set env <set|remove>");
                        }
                    }

                    default ->
                            executor.sendMessage(
                                    "§cUsage: "
                                            + executor.getCommandPrefix()
                                            + "server "
                                            + serverArg
                                            + " set <name|autostart|memory|env>");
                }
            }

            default ->
                    executor.sendMessage(
                            "§cUsage: "
                                    + executor.getCommandPrefix()
                                    + "server "
                                    + serverArg
                                    + " <start|stop|restart|kill|delete|set>");
        }
    }
}
