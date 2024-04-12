package de.obsidiancloud.common.config.serializer;

import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlConfigSerializer implements ConfigSerializer {
    @Override
    public String serialize(Map<String, Object> map) {
        return new Yaml().dumpAs(map, null, DumperOptions.FlowStyle.BLOCK);
    }

    @Override
    public Map<String, Object> deserialize(String string) {
        return new Yaml().load(string);
    }
}
