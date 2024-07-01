package de.obsidiancloud.node.config.serializer;

import java.util.Map;

public interface ConfigSerializer {
    public String serialize(Map<String, Object> map);

    public Map<String, Object> deserialize(String string);
}
