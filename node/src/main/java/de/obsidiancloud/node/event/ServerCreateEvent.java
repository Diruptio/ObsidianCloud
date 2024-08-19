package de.obsidiancloud.node.event;

import de.obsidiancloud.common.event.Cancellable;
import de.obsidiancloud.node.local.LocalOCServer;
import org.jetbrains.annotations.NotNull;

/** This event is called before a server is created. */
public class ServerCreateEvent implements Cancellable {
    private boolean cancelled = false;
    private final @NotNull LocalOCServer server;

    /**
     * Create a new {@link ServerCreateEvent}.
     *
     * @param server The server that will be created.
     */
    public ServerCreateEvent(@NotNull LocalOCServer server) {
        this.server = server;
    }

    /**
     * Get the server that will be created.
     *
     * @return The server that will be created.
     */
    public @NotNull LocalOCServer getServer() {
        return server;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
