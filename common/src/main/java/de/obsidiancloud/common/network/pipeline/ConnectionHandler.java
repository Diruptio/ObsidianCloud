package de.obsidiancloud.common.network.pipeline;

import de.obsidiancloud.common.network.NetworkHandler;
import de.obsidiancloud.common.network.Packet;
import de.obsidiancloud.common.network.packets.HandshakePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Miles
 * @since 02.06.2024
 */
@ChannelHandler.Sharable
@RequiredArgsConstructor
@Getter
public class ConnectionHandler extends SimpleChannelInboundHandler<Packet> {

    private final List<Packet> backlog = new CopyOnWriteArrayList<>();
    private final String id;
    private final boolean client;

    @Setter private Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.channel = ctx.channel();
        if (client) {
            backlog.forEach(this::send);
            System.out.println("[" + id + "] Sent backlog: " + backlog.size());
            backlog.clear();
        }

        NetworkHandler.getConnectionRegistry().addConnection(id, ctx);
        System.out.println("[" + id + "] Added connection: " + id);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        final String connectionId = NetworkHandler.getConnectionRegistry().removeConnection(ctx);
        if (connectionId != null) {
            System.out.println("[" + id + "] Removed connection: " + connectionId);
        }
    }

    public void send(Packet packet) {
        final String packetName = packet.getClass().getSimpleName();
        System.out.println("[" + id + "] Sending packet: " + packetName);
        if (channel == null) {
            backlog.add(packet);
            System.out.println("[" + id + "] Added to backlog: " + packetName);
            return;
        }

        channel.writeAndFlush(packet);
        System.out.println("[" + id + "] Sent packet: " + packetName);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        System.out.println("[" + id + "] Handling received packet: " + packet.toString());

        if (!client) {
            if (packet instanceof HandshakePacket) {
                final String connectionId = ((HandshakePacket) packet).getId();
                NetworkHandler.getConnectionRegistry().addConnection(connectionId, ctx);
                System.out.println("[" + id + "] Added connection: " + connectionId);
                return;
            }

            if (packet.getTargetConnectionId() != null) {
                Optional<ChannelHandlerContext> connection =
                        NetworkHandler.getConnectionRegistry()
                                .getConnection(packet.getTargetConnectionId());
                if (connection.isEmpty()) {
                    System.out.println(
                            "["
                                    + id
                                    + "] Could not forward packet to "
                                    + packet.getTargetConnectionId()
                                    + ": Connection was not found");
                    return;
                }

                connection.get().writeAndFlush(packet);
                System.out.println(
                        "[" + id + "] Forwarded packet to " + packet.getTargetConnectionId());
                return;
            }
        }

        NetworkHandler.getPacketRegistry()
                .getPacketListeners(packet.getClass())
                .forEach(listener -> listener.handle(packet, this));
    }
}
