package de.obsidiancloud.common.network.pipeline;

import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Objects;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerChannelHandler extends SimpleChannelInboundHandler<Packet> {
    private final @NotNull Consumer<Connection> clientConnectedCallback;
    private @Nullable Connection connection;

    public ServerChannelHandler(@NotNull Consumer<Connection> clientConnectedCallback) {
        this.clientConnectedCallback = clientConnectedCallback;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        connection = new Connection();
        connection.setChannel(ctx.channel());
        clientConnectedCallback.accept(connection);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        Objects.requireNonNull(connection).accept(packet);
    }
}
