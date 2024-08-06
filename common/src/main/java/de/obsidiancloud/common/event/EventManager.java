package de.obsidiancloud.common.event;

import de.obsidiancloud.common.event.annotation.EventHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public class EventManager {
    private static final Map<Integer, Map<Class<? extends Event>, List<EventData>>> events =
            new ConcurrentHashMap<>();

    public static void register(Class<? extends Event> eventClass, Method method, Object o) {
        if (isMethodBad(method)) {
            return;
        }

        EventData methodData =
                new EventData(o, method, method.getAnnotation(EventHandler.class).priority());
        method.setAccessible(true);

        try {
            Event event = eventClass.getDeclaredConstructor().newInstance();
            int eventId = event.getId();

            events.computeIfAbsent(eventId, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(eventClass, k -> new ArrayList<>())
                    .add(methodData);

            sortListValue(eventClass);

        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Failed to register event method", e);
        }
    }

    /** Register the class which is using the @EventHandler */
    public static void registerEvents(Class<?> @NotNull ... events) {
        for (Class<?> eventTarget : events) {
            register(eventTarget);
        }
    }

    public static List<EventData> getEventData(int id, Class<? extends Event> clazz) {
        Map<Class<? extends Event>, List<EventData>> eventMap = events.get(id);
        return eventMap != null
                ? eventMap.getOrDefault(clazz, Collections.emptyList())
                : Collections.emptyList();
    }

    /** used for sorting */
    private static void sortListValue(@NotNull Class<? extends Event> clazz) {
        try {
            Map<Class<? extends Event>, List<EventData>> eventMap;
            eventMap = events.get(clazz.newInstance().getId());
            if (eventMap != null) {
                List<EventData> eventDataList = eventMap.get(clazz);
                if (eventDataList != null) {
                    eventDataList.sort(Comparator.comparingInt(EventData::priority));
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<Class<? extends Event>, List<EventData>> get(int id) {
        return events.get(id);
    }

    private static boolean isMethodBad(@NotNull Method method) {
        return method.getParameterCount() != 1 || !method.isAnnotationPresent(EventHandler.class);
    }

    public static void register(@NotNull Object targetObj) {
        for (Method method : targetObj.getClass().getMethods()) {
            if (method.getParameterCount() > 0 && method.isAnnotationPresent(EventHandler.class)) {
                register((Class<? extends Event>) method.getParameterTypes()[0], method, targetObj);
            }
        }
    }
}
