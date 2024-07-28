package de.obsidiancloud.common.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A storage for events. */
public class Storage {
    private final @NotNull Class<?> eventClass;
    private final @NotNull Listener<Object> listener;
    private @Nullable Storage next;

    /**
     * Create a new Storage.
     *
     * @param eventClass The class of the event.
     * @param listener The listener that should be called.
     * @param next The next storage.
     */
    @SuppressWarnings("unchecked")
    public Storage(
            @NotNull Class<?> eventClass, @NotNull Listener<?> listener, @Nullable Storage next) {
        this.eventClass = eventClass;
        this.listener = (Listener<Object>) listener;
        this.next = next;
    }

    /**
     * Call the listener if the event is the right class.
     *
     * @param event The event to call.
     */
    public void call(@NotNull Object event) {
        if (eventClass == event.getClass()) {
            try {
                listener.call(event);
            } catch (Throwable throwable) {
                throwable.printStackTrace(System.err);
            }
        }
        if (next != null) next.call(event);
    }

    /**
     * Get the listener.
     *
     * @return The listener.
     */
    public @NotNull Listener<Object> getListener() {
        return listener;
    }

    /**
     * Get the next storage.
     *
     * @return The next storage.
     */
    public @Nullable Storage getNext() {
        return next;
    }

    /**
     * Set the next storage.
     *
     * @param next The next storage.
     */
    public void setNext(@Nullable Storage next) {
        this.next = next;
    }
}
