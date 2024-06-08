package de.obsidiancloud.protocol.registry;

import io.netty.channel.ChannelHandlerContext;

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
            connections.put(id, ctx);
        }
    }

    public void removeConnection(String id) {
        connections.remove(id.toLowerCase());
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
