package de.obsidiancloud.common.event;

import java.lang.reflect.Method;

public record EventData(Object src, Method target, byte priority) {}
