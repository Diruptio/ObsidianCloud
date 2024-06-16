package de.obsidiancloud.common.network.pipeline;

import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class ClientChannelHandler extends SimpleChannelInboundHandler<Packet> {
    private final @NotNull Connection connection;

    public ClientChannelHandler(@NotNull Connection connection) {
        this.connection = connection;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        connection.setChannel(ctx.channel());
        while (!connection.getBacklog().isEmpty()) {
            connection.send(connection.getBacklog().remove());
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        Objects.requireNonNull(connection).accept(packet);
    }
}
