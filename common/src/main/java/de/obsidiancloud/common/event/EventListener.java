package de.obsidiancloud.common.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {
    Priority priority() default Priority.NORMAL;

    enum Priority {
        LOWEST(1),
        LOW(2),
        NORMAL(3),
        HIGH(4),
        HIGHEST(5),
        MONITOR(6);

        private final byte value;

        Priority(int value) {
            this.value = (byte) value;
        }

        public byte getValue() {
            return value;
        }
    }
}
