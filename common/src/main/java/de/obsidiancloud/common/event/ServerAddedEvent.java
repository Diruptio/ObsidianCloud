package de.obsidiancloud.common.event;

import de.obsidiancloud.common.OCServer;
import org.jetbrains.annotations.NotNull;

/** Called after a server was created. */
public class ServerAddedEvent {
    private final @NotNull OCServer server;

    /**
     * Create a new {@link ServerAddedEvent}.
     *
     * @param server The server that was created.
     */
    public ServerAddedEvent(@NotNull OCServer server) {
        this.server = server;
    }

    /**
     * Get the server that was created.
     *
     * @return The server that was created.
     */
    public @NotNull OCServer getServer() {
        return server;
    }
}
