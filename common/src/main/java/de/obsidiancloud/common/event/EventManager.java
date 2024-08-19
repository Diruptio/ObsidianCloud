package de.obsidiancloud.common.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The event manager. */
public class EventManager {
    private static Storage head = null;

    /**
     * Call the event.
     *
     * @param event The event to call.
     */
    public static void call(@NotNull Object event) {
        call(null, event);
    }

    /**
     * Call the event.
     *
     * @param owner The owner of the listener.
     * @param event The event to call.
     */
    public static void call(@Nullable Object owner, @NotNull Object event) {
        if (head != null) head.call(owner, event);
    }

    /** Delete every listener. */
    public static void clear() {
        head = null;

        for (EventHandler.Priority value : EventHandler.Priority.values()) {
            value.setMarker(null);
        }
    }

    /**
     * Unregister a listener.
     *
     * @param owner The owner of the listener.
     */
    public static void unregister(@NotNull Object owner) {
        unregister(owner, null);
    }

    /**
     * Unregister a listener.
     *
     * @param eventClass The event class.
     */
    public static <T> void unregister(@NotNull Class<T> eventClass) {
        unregister(null, eventClass);
    }

    /**
     * Unregister a listener.
     *
     * @param owner The owner of the listener.
     */
    public static <T> void unregister(@Nullable Object owner, @Nullable Class<T> eventClass) {
        if (head != null) {
            if ((owner == null || owner.equals(head.getOwner()))
                    && (eventClass == null || eventClass.equals(head.getEventClass()))) {
                EventHandler.Priority priority = EventHandler.Priority.findPriority(head);

                if (priority != null) {
                    priority.setMarker(null);
                }

                head = head.getNext();
            }

            if (head != null) {
                head.unregister(owner, eventClass);
            }
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
                        listener,
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
     * @param owner The owner of the listener.
     * @param eventClass The event class.
     * @param listener The listener to register.
     */
    public static <T> void register(Object owner, Class<T> eventClass, Listener<T> listener) {
        register(owner, eventClass, listener, EventHandler.Priority.NORMAL);
    }

    /**
     * Register a listener.
     *
     * @param <T> The event type.
     * @param eventClass The event class.
     * @param listener The listener to register.
     */
    public static <T> void register(Class<T> eventClass, Listener<T> listener) {
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
            Class<T> eventClass, Listener<T> listener, EventHandler.Priority priority) {
        register(null, eventClass, listener, priority);
    }

    /**
     * Register a listener.
     *
     * @param <T> The event type.
     * @param owner The owner of the listener.
     * @param eventClass The event class.
     * @param listener The listener to register.
     * @param priority The priority of the listener.
     */
    public static <T> void register(
            Object owner,
            Class<T> eventClass,
            Listener<T> listener,
            EventHandler.Priority priority) {
        Storage marker = priority.getExistingMarker();
        if (head == null) {
            head = new Storage(owner, eventClass, listener, null);
            priority.setMarker(head);
        } else if (marker == null) {
            head = new Storage(owner, eventClass, listener, head);
            priority.setMarker(head);
        } else {
            marker.setNext(new Storage(owner, eventClass, listener, marker.getNext()));
            priority.setMarker(marker.getNext());
        }
    }
}
