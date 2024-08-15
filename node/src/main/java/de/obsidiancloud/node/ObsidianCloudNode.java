package de.obsidiancloud.node;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.command.BaseCommandProvider;
import de.obsidiancloud.common.command.Command;
import de.obsidiancloud.common.command.impl.HelpCommand;
import de.obsidiancloud.common.config.Config;
import de.obsidiancloud.common.config.ConfigProperty;
import de.obsidiancloud.common.config.ConfigSection;
import de.obsidiancloud.common.console.Console;
import de.obsidiancloud.common.console.ConsoleCommandExecutor;
import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.NetworkHandler;
import de.obsidiancloud.common.network.NetworkServer;
import de.obsidiancloud.common.network.packets.CustomMessagePacket;
import de.obsidiancloud.common.network.packets.PlayerKickPacket;
import de.obsidiancloud.common.network.packets.PlayerMessagePacket;
import de.obsidiancloud.node.command.KickCommand;
import de.obsidiancloud.node.command.ListCommand;
import de.obsidiancloud.node.command.ScreenCommand;
import de.obsidiancloud.node.command.ShutdownCommand;
import de.obsidiancloud.node.local.LocalOCNode;
import de.obsidiancloud.node.local.LocalOCServer;
import de.obsidiancloud.node.local.template.OCTemplate;
import de.obsidiancloud.node.local.template.TemplateProvider;
import de.obsidiancloud.node.local.template.paper.PaperTemplateProvider;
import de.obsidiancloud.node.local.template.platform.PlatformTemplateProvider;
import de.obsidiancloud.node.local.template.purpur.PurpurTemplateProvider;
import de.obsidiancloud.node.local.template.simple.SimpleTemplateProvider;
import de.obsidiancloud.node.local.template.velocity.VelocityTemplateProvider;
import de.obsidiancloud.node.network.listener.S2NHandshakeListener;
import de.obsidiancloud.node.network.packets.N2SSyncPacket;
import de.obsidiancloud.node.network.packets.S2NHandshakePacket;
import de.obsidiancloud.node.network.packets.S2NPlayerJoinPacket;
import de.obsidiancloud.node.network.packets.S2NPlayerLeavePacket;
import de.obsidiancloud.node.threads.NodeThread;
import de.obsidiancloud.node.util.TaskParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public class ObsidianCloudNode {
    private static final Logger logger = Logger.getLogger("main");
    private static final ConsoleCommandExecutor executor = new ConsoleCommandExecutor(logger);
    private static Console console;
    private static Config config;
    private static ConfigProperty<String> clusterKey;
    private static NodeObsidianCloudAPI api;
    private static final BaseCommandProvider commandProvider = new BaseCommandProvider();
    private static final List<TemplateProvider> templateProviders = new ArrayList<>();
    private static NodeThread nodeThread;
    private static NetworkServer networkServer;

    public static void main(String[] args) {
        try {
            Files.createDirectories(Path.of("logs"));
            LogManager.getLogManager()
                    .readConfiguration(ClassLoader.getSystemResourceAsStream("logging.properties"));
            console = new Console(logger, executor);
            console.start();
            loadConfig();
            List<LocalOCServer> servers = loadServersConfig();
            api = new NodeObsidianCloudAPI(loadLocalNode(servers));
            ObsidianCloudAPI.setInstance(api);
            reload();
            nodeThread = new NodeThread();
            nodeThread.start();

            registerPackets();
            ConfigSection localNode = Objects.requireNonNull(config.getSection("local_node"));
            String host = localNode.getString("host", "0.0.0.0");
            int port = localNode.getInt("port", 3005);
            networkServer = new NetworkServer(host, port, ObsidianCloudNode::clientConnected);
            networkServer.start();
        } catch (Throwable exception) {
            exception.printStackTrace(System.err);
        }
    }

    private static void clientConnected(@NotNull Connection connection) {
        connection.addPacketListener(new S2NHandshakeListener());
    }

    private static void loadConfig() {
        config = new Config(Path.of("config.yml"), Config.Type.YAML);
        StringBuilder clusterKey = new StringBuilder();
        for (int i = 0; i < 32; i++) clusterKey.append((char) ('a' + new Random().nextInt(26)));
        ObsidianCloudNode.clusterKey =
                new ConfigProperty<>(config, "clusterkey", clusterKey.toString());
        config.setDefault("local_node", new HashMap<>());
        ConfigSection localNode = Objects.requireNonNull(config.getSection("local_node"));
        localNode.setDefault("name", "Node-1");
        localNode.setDefault("host", "0.0.0.0");
        localNode.setDefault("port", 3005);
    }

    private static List<LocalOCServer> loadServersConfig() {
        Config serversConfig = new Config(Path.of("servers.yml"), Config.Type.YAML);
        List<LocalOCServer> servers = new ArrayList<>();
        for (String name : serversConfig.getData().keySet()) {
            try {
                ConfigSection section = Objects.requireNonNull(serversConfig.getSection(name));
                String task = section.getString("task");
                OCServer.Type type = OCServer.Type.valueOf(section.getString("type"));
                boolean autoStart = section.getBoolean("auto_start");
                String executable = Objects.requireNonNull(section.getString("executable"));
                int memory = section.getInt("memory");
                List<String> jvmArgs = section.getList("jvm_args", new ArrayList<>());
                List<String> args = section.getList("args", new ArrayList<>());
                Map<String, String> environmentVariables = new HashMap<>();
                ConfigSection environmentVariablesSection =
                        Objects.requireNonNull(section.getSection("environment_variables"));
                environmentVariablesSection
                        .getData()
                        .forEach((key, value) -> environmentVariables.put(key, value.toString()));
                int port = section.getInt("port");
                servers.add(
                        new LocalOCServer(
                                task,
                                name,
                                type,
                                OCServer.LifecycleState.OFFLINE,
                                OCServer.Status.OFFLINE,
                                autoStart,
                                false,
                                executable,
                                memory,
                                jvmArgs,
                                args,
                                environmentVariables,
                                port));
            } catch (Throwable ignored) {
            }
        }
        return servers;
    }

    private static LocalOCNode loadLocalNode(List<LocalOCServer> servers) {
        ConfigSection localNode = Objects.requireNonNull(config.getSection("local_node"));
        String name = Objects.requireNonNull(localNode.getString("name"));
        return new LocalOCNode(name, servers);
    }

    public static void reload() {
        loadTemplateProviders();
        registerCommands();
        loadTasks();
    }

    private static void registerCommands() {
        Command.registerProvider(commandProvider);
        commandProvider.registerCommand(new HelpCommand());
        commandProvider.registerCommand(new KickCommand());
        commandProvider.registerCommand(new ListCommand());
        commandProvider.registerCommand(new ScreenCommand());
        commandProvider.registerCommand(new ShutdownCommand());
    }

    private static void loadTemplateProviders() {
        templateProviders.clear();
        templateProviders.add(new SimpleTemplateProvider());
        templateProviders.add(new PlatformTemplateProvider());
        templateProviders.add(new PaperTemplateProvider());
        templateProviders.add(new PurpurTemplateProvider());
        // TODO: FabricTemplateProvider
        templateProviders.add(new VelocityTemplateProvider());
    }

    private static void loadTasks() {
        api.getTasks().clear();
        try (Stream<Path> files = Files.list(Path.of("tasks"))) {
            for (Path file : (Iterable<Path>) files::iterator) {
                try {
                    api.getTasks().add(TaskParser.parse(file));
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable exception) {
            logger.log(Level.SEVERE, "Failed to load tasks", exception);
        }
    }

    private static void registerPackets() {
        NetworkHandler.getPacketRegistry().registerPacket(CustomMessagePacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(PlayerKickPacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(PlayerMessagePacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(N2SSyncPacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(S2NHandshakePacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(S2NPlayerJoinPacket.class);
        NetworkHandler.getPacketRegistry().registerPacket(S2NPlayerLeavePacket.class);
    }

    /** Shuts down the node. */
    public static void shutdown() {
        nodeThread.interrupt();
        api.getLocalNode().getServers().forEach(OCServer::stop);
        networkServer.close();
        if (console != null) console.stop();
    }

    /**
     * Gets the main logger.
     *
     * @return The main logger.
     */
    public static @NotNull Logger getLogger() {
        return logger;
    }

    /**
     * Gets the cluster key config property.
     *
     * @return The cluster key config property.
     */
    public static @NotNull ConfigProperty<String> getClusterKey() {
        return clusterKey;
    }

    /**
     * Gets a template by its name.
     *
     * @param name The name of the template.
     * @return The template or null if no template with the specified name exists.
     */
    public static OCTemplate getTemplate(@NotNull String name) {
        for (TemplateProvider provider : templateProviders) {
            OCTemplate template = provider.getTemplate(name);
            if (template != null) return template;
        }
        return null;
    }

    /**
     * Gets the network server.
     *
     * @return The network server.
     */
    public static NetworkServer getNetworkServer() {
        return networkServer;
    }
}
