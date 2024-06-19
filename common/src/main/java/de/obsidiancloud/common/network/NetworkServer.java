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

/** The network server for ObsidianCloud. */
public class NetworkServer extends Thread {
    private final Logger logger = Logger.getLogger("NetworkServer");
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
     * @param clientConnectedCallback The callback which is called when a client connects
     */
    public NetworkServer(
            @NotNull String host, int port, @NotNull Consumer<Connection> clientConnectedCallback) {
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

    /** Close the server and all connections. */
    public void close() {
        connections.forEach(Connection::close);
        channel.close();
    }

    /**
     * Get all connections.
     *
     * @return The connections.
     */
    public @NotNull List<Connection> getConnections() {
        return connections;
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
