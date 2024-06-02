package de.obsidiancloud.protocol.registry;

import de.obsidiancloud.protocol.pipeline.ConnectionHandler;
import lombok.extern.java.Log;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Miles
 * @since 02.06.2024
 */
@Log
public class ConnectionRegistry {

    private final Map<String, ConnectionHandler> connections = new ConcurrentHashMap<>();

    public void addConnection(ConnectionHandler handler) {
        connections.put(handler.getId().toLowerCase(), handler);
        log.info("Added connection: " + handler.getId() + " - " + connections.size());
    }

    public void removeConnection(String id) {
        connections.remove(id.toLowerCase());
        log.info("Removed connection: " + id + " - " + connections.size());
    }

    public Optional<ConnectionHandler> getConnection(String id) {
        return Optional.ofNullable(connections.get(id.toLowerCase()));
    }

    public Collection<ConnectionHandler> getConnections() {
        return connections.values();
    }

    public int countConnections() {
        return connections.size();
    }
}
