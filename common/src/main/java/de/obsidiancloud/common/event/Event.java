package de.obsidiancloud.common.event;

import java.util.*;

public interface Event {
    int getId();

    default void call() {
        Map<Class<? extends Event>, List<EventData>> eventMap = EventManager.get(getId());
        if (eventMap == null) {
            eventMap = new HashMap<>();
        }

        List<EventData> eventDataList = eventMap.get(this.getClass());
        if (eventDataList != null) {
            for (EventData data : eventDataList) {
                try {
                    data.target().invoke(data.src(), this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
