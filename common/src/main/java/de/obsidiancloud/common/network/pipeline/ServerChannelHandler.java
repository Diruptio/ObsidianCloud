package de.obsidiancloud.common.network.pipeline;

import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.Packet;
import de.obsidiancloud.common.network.listener.C2SHandshakeListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class ServerChannelHandler extends SimpleChannelInboundHandler<Packet> {
    private @Nullable Connection connection;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        connection = new Connection();
        connection.setChannel(ctx.channel());
        connection.addPacketListener(new C2SHandshakeListener());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        Objects.requireNonNull(connection).accept(packet);
    }
}
