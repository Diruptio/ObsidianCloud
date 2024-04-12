package de.obsidiancloud.node;

import de.obsidiancloud.common.OCNode;
import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.OCTask;
import de.obsidiancloud.common.command.BaseCommandProvider;
import de.obsidiancloud.common.command.Command;
import de.obsidiancloud.common.command.impl.HelpCommand;
import de.obsidiancloud.common.config.Config;
import de.obsidiancloud.common.config.ConfigSection;
import de.obsidiancloud.common.console.Console;
import de.obsidiancloud.common.console.ConsoleCommandExecutor;
import de.obsidiancloud.node.command.ShutdownCommand;
import de.obsidiancloud.node.local.LocalOCNode;
import de.obsidiancloud.node.local.LocalOCServer;
import de.obsidiancloud.node.local.template.OCTemplate;
import de.obsidiancloud.node.local.template.TemplateProvider;
import de.obsidiancloud.node.local.template.paper.PaperTemplateProvider;
import de.obsidiancloud.node.local.template.purpur.PurpurTemplateProvider;
import de.obsidiancloud.node.local.template.simple.SimpleTemplateProvider;
import de.obsidiancloud.node.remote.RemoteOCNode;
import de.obsidiancloud.node.threads.ServerLoadThread;
import de.obsidiancloud.node.util.TaskParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Node extends BaseCommandProvider {
    private static Node instance;
    private boolean running = true;
    private final Logger logger = Logger.getLogger("main");
    private final Config config = new Config(Path.of("config.yml"), Config.Type.YAML);
    private final Config staticServersConfig = new Config(Path.of("servers.yml"), Config.Type.YAML);
    private final ConsoleCommandExecutor executor = new ConsoleCommandExecutor(logger);
    private Console console;
    private final List<RemoteOCNode> remoteNodes = new ArrayList<>();
    private LocalOCNode localNode;
    private final List<TemplateProvider> templateProviders = new ArrayList<>();
    private final List<OCTask> tasks = new ArrayList<>();

    public static void main(String[] args) {
        try {
            Files.createDirectories(Path.of("logs"));
            LogManager.getLogManager()
                    .readConfiguration(ClassLoader.getSystemResourceAsStream("logging.properties"));
        } catch (Throwable error) {
            Logger.getGlobal().log(Level.SEVERE, "Failed to setup logging", error);
            return;
        }
        new Node();
    }

    public Node() {
        instance = this;
        try {
            console = new Console(logger, executor);
            console.start();
        } catch (Throwable error) {
            logger.log(Level.SEVERE, "Failed to create console", error);
        }

        Command.registerProvider(this);
        registerCommand(new HelpCommand());
        registerCommand(new ShutdownCommand());
        // TODO: servers command
        // TODO: server command
        // TODO: task command

        setupLocalNodeConfig();
        try {
            loadTasks();
            for (OCTask task : tasks) {
                logger.info("Loaded task: " + task.name());
            }
            List<LocalOCServer> servers = loadServers();
            localNode = loadLocalNode(servers);
        } catch (Throwable throwable) {
            logger.log(Level.SEVERE, "Failed loading local node data", throwable);
            shutdown();
        }

        templateProviders.add(new SimpleTemplateProvider());
        templateProviders.add(new PaperTemplateProvider());
        templateProviders.add(new PurpurTemplateProvider());

        while (running) {
            try {
                for (OCTask task : getTasks()) {
                    // Count servers
                    int servers = 0;
                    synchronized (localNode.getServers()) {
                        for (OCServer server : localNode.getServers()) {
                            if (task.name().equals(server.getTask())) {
                                servers++;
                            }
                        }
                    }

                    while (servers < task.minAmount()) {
                        // Find name
                        int n = 1;
                        while (getServer(task.name() + "-" + n) != null) {
                            n++;
                        }
                        String name = task.name() + "-" + n;

                        // Create server instance
                        LocalOCServer server =
                                new LocalOCServer(
                                        task.name(),
                                        name,
                                        OCServer.Status.LOADING,
                                        task.type(),
                                        task.port(),
                                        task.maxPlayers(),
                                        task.autoStart(),
                                        task.autoDelete(),
                                        task.memory(),
                                        task.environmentVariables(),
                                        false);
                        getLocalNode().getServers().add(server);

                        // Load server
                        new ServerLoadThread(server, task.templates()).start();

                        servers++;
                    }
                }

                // Start servers
                for (OCServer server : localNode.getServers()) {
                    if (server.isAutoStart() && server.getStatus() == OCServer.Status.OFFLINE) {
                        server.start();
                    }
                }

                // TODO: Delete servers if autoDelete is true and server is offline

                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void setupLocalNodeConfig() {
        if (config.getSection("local_node") == null) {
            config.set("local_node", new HashMap<>());
            config.save();
        }
        ConfigSection localNode = config.getSection("local_node");
        assert localNode != null;
        if (localNode.getString("name") == null) {
            localNode.set("name", "Node-1");
            config.save();
        }
        if (localNode.getString("host") == null) {
            localNode.set("host", "127.0.0.1");
            config.save();
        }
        if (localNode.getInt("port") == 0) {
            localNode.set("port", 1405);
            config.save();
        }
    }

    private void loadTasks() {
        tasks.clear();
        try (Stream<Path> files = Files.list(Path.of("tasks"))) {
            for (Path file : (Iterable<Path>) files::iterator) {
                try {
                    tasks.add(TaskParser.parse(file));
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable exception) {
            getLogger().log(Level.SEVERE, "Failed to load tasks", exception);
        }
    }

    private List<LocalOCServer> loadServers() {
        List<LocalOCServer> servers = new ArrayList<>();

        if (Files.notExists(staticServersConfig.getFile())) {
            staticServersConfig.save();
        }

        for (String name : staticServersConfig.getData().keySet()) {
            try {
                ConfigSection section = staticServersConfig.getSection(name);
                assert section != null;
                String task = section.getString("task");
                OCServer.Type type = OCServer.Type.valueOf(section.getString("type"));
                int port = section.getInt("port");
                int maxPlayers = section.getInt("max_players");
                boolean autoStart = section.getBoolean("auto_start");
                int memory = section.getInt("memory");
                Map<String, String> environmentVariables = new HashMap<>();
                ConfigSection environmentVariablesSection =
                        section.getSection("environment_variables");
                assert environmentVariablesSection != null;
                environmentVariablesSection
                        .getData()
                        .forEach((key, value) -> environmentVariables.put(key, value.toString()));
                boolean maintenance = section.getBoolean("maintenance");
                servers.add(
                        new LocalOCServer(
                                task,
                                name,
                                OCServer.Status.OFFLINE,
                                type,
                                port,
                                maxPlayers,
                                autoStart,
                                false,
                                memory,
                                environmentVariables,
                                maintenance));
            } catch (Throwable ignored) {
            }
        }

        return servers;
    }

    private LocalOCNode loadLocalNode(List<LocalOCServer> servers) {
        ConfigSection localNode = config.getSection("local_node");
        assert localNode != null;
        String name = localNode.getString("name");
        assert name != null;
        String host = localNode.getString("host");
        assert host != null;
        int port = localNode.getInt("port");
        assert 1024 < port && port < 65535;
        return new LocalOCNode(name, host, port, servers);
    }

    /** Shuts down the node. */
    public void shutdown() {
        running = false;
        if (console != null) console.stop();
    }

    /**
     * Gets the logger of the node.
     *
     * @return The logger of the node.
     */
    public @NotNull Logger getLogger() {
        return logger;
    }

    /**
     * Gets the console command executor of the node.
     *
     * @return The console command executor of the node.
     */
    public @NotNull ConsoleCommandExecutor getExecutor() {
        return executor;
    }

    /**
     * Gets the console of the node.
     *
     * @return The console of the node.
     */
    public @NotNull Console getConsole() {
        return console;
    }

    /**
     * Gets the remote nodes.
     *
     * @return A {@code List<RemoteOCNode>} of the remote nodes.
     */
    public synchronized @NotNull List<RemoteOCNode> getRemoteNodes() {
        return remoteNodes;
    }

    /**
     * Gets the local node of the node.
     *
     * @return The local node of the node.
     */
    public synchronized @NotNull LocalOCNode getLocalNode() {
        return localNode;
    }

    /**
     * Gets all connected nodes.
     *
     * @return A {@code List<OCNode>} of all connected nodes.
     */
    public synchronized @NotNull List<OCNode> getConnectedNodes() {
        List<OCNode> nodes = new ArrayList<>();
        nodes.add(localNode);
        nodes.addAll(remoteNodes.stream().filter(OCNode::isConnected).toList());
        return nodes;
    }

    /**
     * Gets the config of the node.
     *
     * @return The config of the node.
     */
    public synchronized @NotNull Config getConfig() {
        return config;
    }

    /**
     * Gets the static servers config of the node.
     *
     * @return The static servers config of the node.
     */
    public @NotNull Config getStaticServersConfig() {
        return staticServersConfig;
    }

    /**
     * Gets the console of the node.
     *
     * @return The console of the node.
     */
    public static @NotNull Node getInstance() {
        return instance;
    }

    /**
     * Gets the template providers of the node.
     *
     * @return The template providers of the node.
     */
    public synchronized @NotNull List<TemplateProvider> getTemplateProviders() {
        return templateProviders;
    }

    /**
     * Gets the tasks of the node.
     *
     * @return The tasks of the node.
     */
    public synchronized @NotNull List<OCTask> getTasks() {
        return tasks;
    }

    /**
     * Gets a template by its name.
     *
     * @param name The name of the template.
     * @return The template with the given name or {@code null} if no template with the given name
     *     was found.
     */
    public synchronized @Nullable OCTemplate getTemplate(String name) {
        for (TemplateProvider provider : templateProviders) {
            OCTemplate template = provider.getTemplate(name);
            if (template != null) {
                return template;
            }
        }
        return null;
    }

    /**
     * Gets a server by its name.
     *
     * @param name The name of the server.
     * @return The server with the given name or {@code null} if no server with the given name was
     *     found.
     */
    public synchronized @Nullable OCServer getServer(String name) {
        for (OCNode node : getConnectedNodes()) {
            for (OCServer server : Objects.requireNonNull(node.getServers())) {
                if (server.getName().equals(name)) {
                    return server;
                }
            }
        }
        return null;
    }
}
