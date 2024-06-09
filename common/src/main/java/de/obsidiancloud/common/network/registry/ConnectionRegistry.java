package de.obsidiancloud.common.network.registry;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Miles
 * @since 02.06.2024
 */
public class ConnectionRegistry {

    private final Map<String, ChannelHandlerContext> connections = new ConcurrentHashMap<>();

    public synchronized void addConnection(String id, ChannelHandlerContext ctx) {
        id = id.toLowerCase();
        if (!connections.containsKey(id)) {
            ctx.channel().attr(AttributeKey.valueOf("id")).set(id);
            connections.put(id, ctx);
        }
    }

    public String removeConnection(ChannelHandlerContext ctx) {
        if (ctx.channel() == null) {
            return null;
        }

        final Attribute<String> connectionIdAttr = ctx.channel().attr(AttributeKey.valueOf("id"));
        if (connectionIdAttr == null) {
            return null;
        }

        final String connectionId = connectionIdAttr.get();
        if (connectionId != null) {
            connections.remove(connectionId);
        }

        return connectionId;
    }

    public Optional<ChannelHandlerContext> getConnection(String id) {
        return Optional.ofNullable(connections.get(id.toLowerCase()));
    }

    public Collection<ChannelHandlerContext> getConnections() {
        return connections.values();
    }

    public int countConnections() {
        return connections.size();
    }
}
