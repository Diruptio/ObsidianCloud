package de.obsidiancloud.common.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A storage for events. */
public class Storage {
    private final @Nullable Object owner;
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
            @Nullable Object owner, @NotNull Class<?> eventClass, @NotNull Listener<?> listener, @Nullable Storage next) {
        this.owner = owner;
        this.eventClass = eventClass;
        this.listener = (Listener<Object>) listener;
        this.next = next;
    }

    /**
     * Call the listener if the event is the right class.
     *
     * @param event The event to call.
     */
    public void call(@Nullable Object owner, @NotNull Object event) {
        if ((owner == null || owner.equals(this.owner)) && eventClass == event.getClass()) {
            try {
                listener.call(event);
            } catch (Throwable throwable) {
                throwable.printStackTrace(System.err);
            }
        }
        if (next != null) next.call(owner, event);
    }

    /**
     * Unregister a listener.
     *
     * @param owner The owner of the listener.
     * @param eventClass The event class of the listener.
     */
    public <T> void unregister(@Nullable Object owner, @Nullable Class<T> eventClass) {
        if (next != null) {
            if ((owner == null || owner.equals(next.owner))
                    && (eventClass == null || eventClass.equals(next.eventClass))) {
                final EventHandler.Priority priority = EventHandler.Priority.findPriority(next);

                if (priority != null) {
                    if (EventHandler.Priority.findPriority(this) != null) {
                        priority.setMarker(null);
                    } else {
                        priority.setMarker(this);
                    }
                }

                if (next.next != null) {
                    next = next.next;
                } else {
                    next = null;
                }
            }

            if (next != null) {
                next.unregister(owner, eventClass);
            }
        }
    }

    /**
     * Get the owner.
     *
     * @return The owner.
     */
    public @Nullable Object getOwner() {
        return owner;
    }

    /**
     * Get the event class.
     *
     * @return The event class.
     */
    public @NotNull Class<?> getEventClass() {
        return eventClass;
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
