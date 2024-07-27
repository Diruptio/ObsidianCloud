package de.obsidiancloud.common.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The event manager. */
public class EventManager {
    private static @Nullable Storage head = null;

    /**
     * Call the event.
     *
     * @param event The event to call.
     */
    public static void call(@NotNull Object event) {
        if (head != null) head.call(event);
    }

    /**
     * Unregister a listener.
     *
     * @param listener The listener to unregister.
     */
    public static void unregister(@NotNull Listener<?> listener) {
        head = unregister(head, listener);
    }

    private static @Nullable Storage unregister(
            @Nullable Storage marker, @NotNull Listener<?> listener) {
        if (marker == null) {
            return null;
        } else if (marker.getListener() == listener) {
            return marker.getNext();
        } else {
            marker.setNext(unregister(marker.getNext(), listener));
            return marker;
        }
    }

    /**
     * Register a listener.
     *
     * @param listener The listener to register.
     */
    public static void register(@NotNull Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            EventHandler eventHandler = method.getAnnotation(EventHandler.class);
            if (eventHandler != null) {
                int parameterCount = method.getParameterCount();
                if (parameterCount != 1) {
                    throw new RuntimeException("Invalid parameter count: " + parameterCount);
                }
                register(
                        method.getParameterTypes()[0],
                        event -> {
                            try {
                                method.invoke(listener, event);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        eventHandler.priority());
            }
        }
    }

    /**
     * Register a listener.
     *
     * @param <T> The event type.
     * @param eventClass The event class.
     * @param listener The listener to register.
     */
    public static <T> void register(@NotNull Class<T> eventClass, @NotNull Listener<T> listener) {
        register(eventClass, listener, EventHandler.Priority.NORMAL);
    }

    /**
     * Register a listener.
     *
     * @param <T> The event type.
     * @param eventClass The event class.
     * @param listener The listener to register.
     * @param priority The priority of the listener.
     */
    public static <T> void register(
            @NotNull Class<T> eventClass,
            @NotNull Listener<T> listener,
            @NotNull EventHandler.Priority priority) {
        Storage marker = priority.getExistingMarker();
        if (head == null) {
            head = new Storage(eventClass, listener, null);
            priority.setMarker(head);
        } else if (marker == null) {
            head = new Storage(eventClass, listener, head);
            priority.setMarker(head);
        } else {
            marker.setNext(new Storage(eventClass, listener, marker.getNext()));
            priority.setMarker(marker.getNext());
        }
    }
}
