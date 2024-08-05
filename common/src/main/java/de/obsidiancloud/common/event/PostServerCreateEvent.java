package de.obsidiancloud.common.event;

import de.obsidiancloud.common.OCServer;
import org.jetbrains.annotations.NotNull;

/** Called after a server was created. */
public class PostServerCreateEvent {
    private final @NotNull OCServer server;

    /**
     * Create a new {@link PostServerCreateEvent}.
     *
     * @param server The server that was created.
     */
    public PostServerCreateEvent(@NotNull OCServer server) {
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
