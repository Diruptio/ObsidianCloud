package de.obsidiancloud.common.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public class EventManager {
    private static final Map<Class<? extends Event>, List<Listener>> listeners =
            new ConcurrentHashMap<>();

    /**
     * Registers a listener.
     *
     * @param obj The object to register
     */
    public static void register(@NotNull Object obj) {
        for (Method method : obj.getClass().getMethods()) {
            register(obj, method);
        }
    }

    /**
     * Registers a listener.
     *
     * @param obj The object to register
     * @param method The method to register
     */
    public static void register(@NotNull Object obj, @NotNull Method method) {
        if (!method.isAnnotationPresent(EventListener.class)) return;
        if (method.getParameterCount() != 1) return;
        method.setAccessible(true);
        register(new Listener(obj, method), method.getParameters()[0].getType());
    }

    @SuppressWarnings("unchecked")
    private static void register(@NotNull Listener listener, @NotNull Class<?> clazz) {
        if (!Event.class.isAssignableFrom(clazz)) return;
        Class<? extends Event> eventClass = (Class<? extends Event>) clazz;
        if (!listeners.containsKey(eventClass)) listeners.put(eventClass, new ArrayList<>());
        List<Listener> listeners = EventManager.listeners.get(eventClass);
        listeners.add(listener);
        sortListeners(listeners);
        for (Class<?> interfaces : eventClass.getInterfaces()) {
            register(listener, interfaces);
        }
        register(eventClass.getSuperclass());
    }

    /**
     * Unregisters a listener.
     *
     * @param obj The object to unregister
     */
    public static void unregister(@NotNull Object obj) {
        for (List<Listener> listeners : listeners.values()) {
            listeners.removeIf(listener -> listener.target().equals(obj));
        }
    }

    /**
     * Unregisters a listener.
     *
     * @param obj The object of the method
     * @param method The method to unregister
     */
    public static void unregister(@NotNull Object obj, @NotNull Method method) {
        for (List<Listener> listeners : listeners.values()) {
            listeners.removeIf(
                    listener -> listener.obj().equals(obj) && listener.target().equals(method));
        }
    }

    /**
     * Calls the event.
     *
     * @param event The event to call
     */
    public static void call(@NotNull Event event) {
        for (Class<? extends Event> clazz : listeners.keySet()) {
            if (clazz.isAssignableFrom(event.getClass())) {
                List<Listener> listeners = EventManager.listeners.get(clazz);
                for (Listener listener : listeners) {
                    try {
                        listener.target().invoke(listener.obj(), event);
                    } catch (Throwable exception) {
                        exception.printStackTrace(System.err);
                    }
                }
            }
        }
    }

    /**
     * Sorts the listeners by their priority.
     *
     * @param listeners The listeners to sort
     */
    private static void sortListeners(@NotNull List<Listener> listeners) {
        listeners.sort(Comparator.comparingInt(EventManager::getListenerPriority));
    }

    private static int getListenerPriority(@NotNull Listener listener) {
        return listener.target().getAnnotation(EventListener.class).priority().getValue();
    }

    private record Listener(Object obj, Method target) {}
}
