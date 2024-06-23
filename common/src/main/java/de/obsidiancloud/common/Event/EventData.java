package de.obsidiancloud.common.Event;

import java.lang.reflect.Method;

public record EventData(Object src, Method target, byte priority) {

}
