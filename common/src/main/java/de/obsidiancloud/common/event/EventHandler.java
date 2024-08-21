package de.obsidiancloud.common.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An annotation to mark a method as an event handler. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
    /**
     * The priority of the event handler.
     *
     * @return The priority of the event handler.
     */
    Priority priority() default Priority.NORMAL;

    /**
     * The priority of the event handler. Call order: LOWEST -> LOW -> NORMAL -> HIGH -> HIGHEST ->
     * MONITOR
     */
    enum Priority {
        /** Called at last place. */
        MONITOR,

        /** Called at fifth place. */
        LOWEST,

        /** Called at fourth place. */
        LOW,

        /** Called at third place. */
        NORMAL,

        /** Called at second place. */
        HIGH,

        /** Called first. */
        HIGHEST;

        private @Nullable Storage marker = null;

        /**
         * Set the marker for the priority.
         *
         * @param marker The marker.
         */
        public void setMarker(@Nullable Storage marker) {
            this.marker = marker;
        }

        /**
         * Find priority with marker.
         *
         * @param marker The marker.
         * @return The priority, if one found, otherwise {@code null}.
         */
        public static @Nullable Priority findPriority(@NotNull Storage marker) {
            if (marker.equals(MONITOR.marker)) {
                return MONITOR;
            } else if (marker.equals(LOWEST.marker)) {
                return LOWEST;
            } else if (marker.equals(LOW.marker)) {
                return LOW;
            } else if (marker.equals(NORMAL.marker)) {
                return NORMAL;
            } else if (marker.equals(HIGH.marker)) {
                return HIGH;
            } else if (marker.equals(HIGHEST.marker)) {
                return HIGHEST;
            }
            return null;
        }

        /**
         * Get a existing marker.
         *
         * @return An existing marker, if one exists, otherwise {@code null}.
         */
        public @Nullable Storage getExistingMarker() {
            int priority = ordinal();
            if (priority == LOWEST.ordinal()) {
                if (LOWEST.marker != null) return LOWEST.marker;
                priority++;
            }
            if (priority == LOW.ordinal()) {
                if (LOW.marker != null) return LOW.marker;
                priority++;
            }
            if (priority == NORMAL.ordinal()) {
                if (NORMAL.marker != null) return NORMAL.marker;
                priority += 1;
            }
            if (priority == HIGH.ordinal()) {
                if (HIGH.marker != null) return HIGH.marker;
                priority += 1;
            }
            if (priority == HIGHEST.ordinal()) {
                if (HIGHEST.marker != null) return HIGHEST.marker;
                priority++;
            }
            if (priority == MONITOR.ordinal() && MONITOR.marker != null) {
                return MONITOR.marker;
            }
            return null;
        }
    }
}
