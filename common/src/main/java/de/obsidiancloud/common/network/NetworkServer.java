package de.obsidiancloud.common.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ServerChannel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The network server for ObsidianCloud. */
public class NetworkServer extends Thread {
    private final Logger logger = Logger.getLogger("NetworkServer");
    private final @NotNull String name;
    private final @NotNull String host;
    private final int port;
    private final @NotNull Consumer<Connection> clientConnectedCallback;
    private final @NotNull List<Connection> connections = new CopyOnWriteArrayList<>();
    private ServerChannel channel;

    /**
     * Create a new NetworkServer.
     *
     * @param host The host
     * @param port The port
     */
    public NetworkServer(
            @NotNull String name,
            @NotNull String host,
            int port,
            @NotNull Consumer<Connection> clientConnectedCallback) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.clientConnectedCallback = clientConnectedCallback;
        setName("NetworkServer");
    }

    @Override
    public void run() {
        try {
            ServerBootstrap serverBootstrap =
                    NetworkHandler.buildServerBootstrap(clientConnectedCallback);

            ChannelFuture f = serverBootstrap.bind(host, port).sync();
            logger.log(Level.INFO, "ObsidianCloud Server started on %s:%d".formatted(host, port));

            channel = (ServerChannel) f.channel();
            channel.closeFuture().sync();
            logger.log(Level.INFO, "ObsidianCloud Server stopped");
        } catch (Exception exception) {
            logger.log(
                    Level.SEVERE, "An error occurred while starting the network server", exception);
        }
    }

    public void close() {
        connections.forEach(Connection::close);
        channel.close();
    }

    public @NotNull List<Connection> getConnections() {
        return connections;
    }

    public void addConnection(@NotNull Connection connection) {
        synchronized (connections) {
            connections.add(connection);
        }
    }

    public void removeConnection(@Nullable Connection connection) {
        if (connection == null) return;
        synchronized (connections) {
            connections.remove(connection);
        }
    }

    /**
     * Gets the host.
     *
     * @return The host.
     */
    public @NotNull String getHost() {
        return host;
    }

    /**
     * Gets the port.
     *
     * @return The port.
     */
    public int getPort() {
        return port;
    }
}
