package de.obsidiancloud.common.event;

/**
 * A listener for events.
 *
 * @param <T> The event type.
 */
@FunctionalInterface
public interface Listener<T> {
    /**
     * Call the listener.
     *
     * @param event The event to call.
     */
    void call(T event);
}
