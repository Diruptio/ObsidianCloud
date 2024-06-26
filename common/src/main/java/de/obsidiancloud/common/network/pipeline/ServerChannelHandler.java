package de.obsidiancloud.common.network.pipeline;

import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Objects;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link ServerChannelHandler} is a {@link SimpleChannelInboundHandler} implementation for the
 * server side.
 */
public class ServerChannelHandler extends SimpleChannelInboundHandler<Packet> {
    private final @NotNull Consumer<Connection> clientConnectedCallback;
    private @Nullable Connection connection;

    /**
     * Create a new {@link ServerChannelHandler} with the given {@link Consumer}.
     *
     * @param clientConnectedCallback The callback which is called when a client connects.
     */
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
