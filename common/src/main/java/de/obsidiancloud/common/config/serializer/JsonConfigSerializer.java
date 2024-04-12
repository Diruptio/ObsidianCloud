package de.obsidiancloud.common.config.serializer;

import com.google.gson.GsonBuilder;
import java.util.Map;

public class JsonConfigSerializer implements ConfigSerializer {
    @Override
    public String serialize(Map<String, Object> map) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(map, Map.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> deserialize(String string) {
        return new GsonBuilder().setPrettyPrinting().create().fromJson(string, Map.class);
    }
}
