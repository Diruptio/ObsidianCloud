package de.obsidiancloud.protocol;

import de.obsidiancloud.protocol.pipeline.ConnectionHandler;
import de.obsidiancloud.protocol.pipeline.Pipeline;
import de.obsidiancloud.protocol.registry.ConnectionRegistry;
import de.obsidiancloud.protocol.registry.PacketRegistry;
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
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author Miles
 * @since 02.06.2024
 */
@Log
public class NetworkHandler {

    private static final EventLoopGroup EVENT_GROUP = (Epoll.isAvailable()
            ? new EpollEventLoopGroup()
            : new NioEventLoopGroup());
    private static final Class<? extends ServerChannel> SERVER_CHANNEL = (Epoll.isAvailable()
            ? EpollServerSocketChannel.class
            : NioServerSocketChannel.class);
    private static final Class<? extends Channel> CLIENT_CHANNEL = (Epoll.isAvailable()
            ? EpollSocketChannel.class
            : NioSocketChannel.class);

    @Getter
    private static final ConnectionRegistry connectionRegistry = new ConnectionRegistry();

    @Getter
    private static final PacketRegistry packetRegistry = new PacketRegistry();

    private static final Map<String, List<Packet>> backlog = new HashMap<>();

    public static ConnectionHandler initializeClientConnection(String connectionId, String host, int port) {
        ConnectionHandler handler = new ConnectionHandler(connectionId, true);
        Bootstrap bootstrap = buildClientBootstrap(handler);
        ChannelFuture future = bootstrap.connect(host, port);
        return handler;
    }

    public static ServerBootstrap buildServerBootstrap(ConnectionHandler handler) {
        return new ServerBootstrap()
                .channel(SERVER_CHANNEL)
                .group(EVENT_GROUP)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_LINGER, 0)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true))
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(8 * 1024, 32 * 1024))
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        Pipeline.prepare(channel, handler);
                    }
                });
    }

    public static Bootstrap buildClientBootstrap(ConnectionHandler handler) {
        return new Bootstrap()
                .group(EVENT_GROUP)
                .channel(CLIENT_CHANNEL)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.config().setOption(ChannelOption.IP_TOS, 0x18);
                        Pipeline.prepare(channel, handler);
                    }
                });
    }

    public static void broadcastPacket(Packet packet) {
        final String packetName = packet.getClass().getSimpleName();
        log.info("Broadcasting packet '" + packetName + "' to " + connectionRegistry.countConnections() + " connections");
        connectionRegistry.getConnections().forEach(connection -> connection.send(packet));
    }

    public static void sendPacket(String connectionId, Packet packet) {
        Optional<ConnectionHandler> connection = connectionRegistry.getConnection(connectionId);
        if (connection.isEmpty()) {
            backlog.computeIfAbsent(connectionId.toLowerCase(),
                    o -> new ArrayList<>()).add(packet);
            log.severe("No connection with id '" + connectionId + "' found. Added to backlog");
            return;
        }

        connection.get().send(packet);
    }

    public static List<Packet> getBacklog(String connectionId) {
        return backlog.getOrDefault(connectionId.toLowerCase(), new ArrayList<>());
    }

    public static void clearBacklog() {
        backlog.clear();
    }

    public static Logger getLogger() {
        return log;
    }
}
