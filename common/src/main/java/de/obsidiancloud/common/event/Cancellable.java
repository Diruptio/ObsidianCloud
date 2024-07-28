package de.obsidiancloud.common.event;

/** Represents a cancelable event. */
public interface Cancellable {
    /**
     * Check if the event is cancelled.
     *
     * @return {@code true} if the event is cancelled, otherwise {@code false}.
     */
    boolean isCancelled();

    /**
     * Set the event to be cancelled.
     *
     * @param cancelled {@code true} if the event should be cancelled, otherwise {@code false}.
     */
    void setCancelled(boolean cancelled);
}
