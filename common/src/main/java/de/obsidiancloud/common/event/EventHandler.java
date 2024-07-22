package de.obsidiancloud.common.event;

import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** An annotation to mark a method as an event handler. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
    /** The priority of the event handler. */
    Priority priority() default Priority.NORMAL;

    /** The priority of the event handler. */
    enum Priority {
        LOWEST,
        LOW,
        NORMAL,
        HIGH,
        HIGHEST,
        MONITOR;

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
