package de.obsidiancloud.protocol.pipeline;

import de.obsidiancloud.protocol.NetworkHandler;
import de.obsidiancloud.protocol.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Miles
 * @since 02.06.2024
 */

@ChannelHandler.Sharable
@Log
@RequiredArgsConstructor
@Getter
public class ConnectionHandler extends SimpleChannelInboundHandler<Packet> {

    private final List<Packet> backlog = new CopyOnWriteArrayList<>();
    private final String id;
    private final boolean client;

    @Setter
    private Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.channel = ctx.channel();
        if (client) {
            backlog.forEach(this::send);
            backlog.clear();
        }

        NetworkHandler.getBacklog(id).forEach(this::send);
        NetworkHandler.clearBacklog();
        NetworkHandler.getConnectionRegistry().addConnection(this);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        NetworkHandler.getConnectionRegistry().removeConnection(id);
    }

    public void send(Packet packet) {
        final String packetName = packet.getClass().getSimpleName();
        log.info("Sending packet: " + packetName);
        if (channel == null) {
            backlog.add(packet);
            log.info("Added to backlog: " + packetName);
            return;
        }

        channel.writeAndFlush(packet);
        log.info("Sent packet: " + packetName);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        NetworkHandler.getPacketRegistry().getPacketListeners(packet.getClass()).forEach(listener -> listener.handle(packet, this));
    }
}
