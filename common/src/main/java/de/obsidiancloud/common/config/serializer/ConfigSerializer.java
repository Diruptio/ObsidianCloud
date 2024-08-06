package de.obsidiancloud.common.config.serializer;

import java.util.Map;

public interface ConfigSerializer {
    String serialize(Map<String, Object> map);

    Map<String, Object> deserialize(String string);
}
