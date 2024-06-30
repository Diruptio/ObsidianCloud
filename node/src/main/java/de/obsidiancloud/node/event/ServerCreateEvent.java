package de.obsidiancloud.node.event;

import de.obsidiancloud.common.event.Cancellable;
import de.obsidiancloud.common.event.Event;
import de.obsidiancloud.node.local.LocalOCServer;
import org.jetbrains.annotations.NotNull;

public class ServerCreateEvent implements Event, Cancellable {
    private final @NotNull LocalOCServer server;
    private boolean cancelled = false;

    /**
     * Create a new {@link ServerCreateEvent}.
     *
     * @param server The server that was created.
     */
    public ServerCreateEvent(@NotNull LocalOCServer server) {
        this.server = server;
    }

    /**
     * Get the server that was created.
     *
     * @return The server that was created.
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
