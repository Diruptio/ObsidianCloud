package de.obsidiancloud.node.event.annotation;

public class EventPriority {
    public static final byte MONITOR = 0, LOW = 1, NORMAL = 2, HIGH = 3, HIGHEST = 4;
    public static final byte[] VALUES = new byte[] {LOW, NORMAL, HIGH, HIGHEST};
}
