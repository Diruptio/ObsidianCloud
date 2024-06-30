package de.obsidiancloud.common.event;

public interface Cancellable {
    /**
     * Gets the cancellation state of this event. A cancelled event will not be executed in the
     * server, but will still pass to other listeners.
     *
     * @return true if this event is cancelled, otherwise false
     */
    boolean isCancelled();

    /**
     * Sets the cancellation state of this event. A cancelled event will not be executed in the
     * server, but will still pass to other listeners.
     *
     * @param cancelled true if you wish to cancel this event, otherwise false
     */
    void setCancelled(boolean cancelled);
}
