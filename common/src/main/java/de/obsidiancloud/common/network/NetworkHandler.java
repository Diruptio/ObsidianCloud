package de.obsidiancloud.common.network;

import de.obsidiancloud.common.network.pipeline.ClientChannelHandler;
import de.obsidiancloud.common.network.pipeline.Pipeline;
import de.obsidiancloud.common.network.pipeline.ServerChannelHandler;
import de.obsidiancloud.common.network.registry.PacketRegistry;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.function.Consumer;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @author Miles
 * @since 02.06.2024
 */
public class NetworkHandler {
    private static final @NotNull Logger logger = Logger.getLogger("NetworkHandler");
    private static final @NotNull EventLoopGroup BOSS_GROUP =
            (Epoll.isAvailable() ? new EpollEventLoopGroup(1) : new NioEventLoopGroup(1));
    private static final @NotNull EventLoopGroup WORKER_GROUP =
            (Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup());
    private static final @NotNull Class<? extends ServerChannel> SERVER_CHANNEL =
            (Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
    private static final @NotNull Class<? extends Channel> CLIENT_CHANNEL =
            (Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class);
    private static final @NotNull PacketRegistry packetRegistry = new PacketRegistry();

    /**
     * Initialize a new client connection.
     *
     * @param host The host
     * @param port The port
     * @return The connection
     */
    public static @NotNull Connection initializeClientConnection(@NotNull String host, int port) {
        Connection connection = new Connection();
        Bootstrap bootstrap = buildClientBootstrap(connection);
        try {
            bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    /**
     * Build a new server bootstrap.
     *
     * @param clientConnectedCallback The callback which is called when a client connects
     * @param clientDisconnectedCallback The callback which is called when a client disconnects
     * @return The server bootstrap
     */
    public static @NotNull ServerBootstrap buildServerBootstrap(
            @NotNull Consumer<Connection> clientConnectedCallback,
            @NotNull Consumer<Connection> clientDisconnectedCallback) {
        return new ServerBootstrap()
                .group(BOSS_GROUP, WORKER_GROUP)
                .channel(SERVER_CHANNEL)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_LINGER, 0)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true))
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(8 * 1024, 32 * 1024))
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        Pipeline.prepare(
                                channel, new ServerChannelHandler(clientConnectedCallback, clientDisconnectedCallback));
                    }
                });
    }

    /**
     * Build a new client bootstrap.
     *
     * @param connection The connection
     * @return The client bootstrap
     */
    public static @NotNull Bootstrap buildClientBootstrap(@NotNull Connection connection) {
        return new Bootstrap().group(WORKER_GROUP).channel(CLIENT_CHANNEL).handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) {
                channel.config().setOption(ChannelOption.IP_TOS, 0x18);
                Pipeline.prepare(channel, new ClientChannelHandler(connection));
            }
        });
    }

    /**
     * Get the packet registry.
     *
     * @return The packet registry
     */
    public static @NotNull PacketRegistry getPacketRegistry() {
        return packetRegistry;
    }

    /**
     * Get the logger of the network handler.
     *
     * @return The logger of the network handler
     */
    public static @NotNull Logger getLogger() {
        return logger;
    }
}
