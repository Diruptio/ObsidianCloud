package de.obsidiancloud.common.config;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigSection {
    protected Map<String, Object> data;

    public ConfigSection(@NotNull Map<String, Object> data) {
        this.data = data;
    }

    /**
     * Checks if the section contains a key.
     *
     * @param key The key to check
     * @return Returns {@code true} if the section contains the key, otherwise {@code false}.
     */
    public boolean contains(@NotNull String key) {
        return data.containsKey(key);
    }

    /**
     * Gets the value of the section.
     *
     * @param key The key of the value.
     * @return Returns the value for the key.
     */
    public Object get(@NotNull String key) {
        return data.get(key);
    }

    /**
     * Gets the value of the section.
     *
     * @param key The key of the value.
     * @param def The default value if the key is not present.
     * @return Returns the value for the key.
     */
    public @Nullable Object get(@NotNull String key, @Nullable Object def) {
        return data.getOrDefault(key, def);
    }

    /**
     * Gets the value of the section.
     *
     * @param key The key of the value.
     * @param type The type of the value.
     * @return Returns the value for the key.
     */
    public <T> @Nullable T get(@NotNull String key, @NotNull Class<T> type) {
        return get(key, type, null);
    }

    /**
     * Gets the value of the section.
     *
     * @param key The key of the value.
     * @param type The type of the value.
     * @param def The default value if the key is not present.
     * @return Returns the value for the key.
     */
    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(@NotNull String key, @NotNull Class<T> type, @Nullable T def) {
        return data.containsKey(key)
                ? type.isAssignableFrom(data.get(key).getClass()) ? (T) data.get(key) : def
                : def;
    }

    /**
     * Gets an integer value of the section.
     *
     * @param key The key of the value.
     * @return Returns the value for the key.
     */
    public int getInt(@NotNull String key) {
        return getInt(key, 0);
    }

    /**
     * Gets an integer value of the section.
     *
     * @param key The key of the value.
     * @return Returns the value for the key.
     */
    public int getInt(@NotNull String key, int def) {
        Object value = get(key);
        return value == null ? def : value instanceof Number num ? num.intValue() : def;
    }

    /**
     * Gets a boolean value of the section.
     *
     * @param key The key of the value.
     * @return Returns the value for the key.
     */
    public boolean getBoolean(@NotNull String key) {
        return getBoolean(key, false);
    }

    /**
     * Gets a boolean value of the section.
     *
     * @param key The key of the value.
     * @param def The default value if the key is not present.
     * @return Returns the value for the key.
     */
    public boolean getBoolean(@NotNull String key, boolean def) {
        Object value = get(key);
        return value == null ? def : value instanceof Boolean bool ? bool : def;
    }

    /**
     * Gets a string value of the section.
     *
     * @param key The key of the value.
     * @return Returns the value for the key.
     */
    public @Nullable String getString(@NotNull String key) {
        return getString(key, null);
    }

    /**
     * Gets a string value of the section.
     *
     * @param key The key of the value.
     * @param def The default value if the key is not present.
     * @return Returns the value for the key.
     */
    public @Nullable String getString(@NotNull String key, @Nullable String def) {
        Object value = get(key);
        return value == null ? def : value instanceof String str ? str : def;
    }

    /**
     * Gets a float value of the section.
     *
     * @param key The key of the value.
     * @return Returns the value for the key.
     */
    public float getFloat(@NotNull String key) {
        return getFloat(key, 0);
    }

    /**
     * Gets a float value of the section.
     *
     * @param key The key of the value.
     * @param def The default value if the key is not present.
     * @return Returns the value for the key.
     */
    public float getFloat(@NotNull String key, float def) {
        Object value = get(key);
        return value == null ? def : value instanceof Number num ? num.floatValue() : def;
    }

    /**
     * Gets a double value of the section.
     *
     * @param key The key of the value.
     * @return Returns the value for the key.
     */
    public double getDouble(@NotNull String key) {
        return getDouble(key, 0);
    }

    /**
     * Gets a double value of the section.
     *
     * @param key The key of the value.
     * @param def The default value if the key is not present.
     * @return Returns the value for the key.
     */
    public double getDouble(@NotNull String key, double def) {
        Object value = get(key);
        return value == null ? def : value instanceof Number num ? num.doubleValue() : def;
    }

    /**
     * Gets a long value of the section.
     *
     * @param key The key of the value.
     * @return Returns the value for the key.
     */
    public long getLong(@NotNull String key) {
        return getLong(key, 0);
    }

    /**
     * Gets a long value of the section.
     *
     * @param key The key of the value.
     * @param def The default value if the key is not present.
     * @return Returns the value for the key.
     */
    public long getLong(@NotNull String key, long def) {
        Object value = get(key);
        return value == null ? def : value instanceof Number num ? num.longValue() : def;
    }

    /**
     * Gets a list value of the section.
     *
     * @param key The key of the value.
     * @return Returns the value for the key.
     */
    public @Nullable List<?> getList(@NotNull String key) {
        return getList(key, null);
    }

    /**
     * Gets a list value of the section.
     *
     * @param key The key of the value.
     * @param def The default value if the key is not present.
     * @return Returns the value for the key.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(@NotNull String key, @Nullable List<T> def) {
        List<T> value = get(key, List.class);
        return value == null ? def : value;
    }

    /**
     * Gets a subsection of the section.
     *
     * @param key The key of the section.
     * @return Returns the subsection of the section.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public @Nullable ConfigSection getSection(@NotNull String key) {
        return data.containsKey(key)
                ? data.get(key) instanceof Map map ? new ConfigSection(map) : null
                : null;
    }

    /**
     * Sets the value of the section.
     *
     * @param key The key of the value.
     * @param value The value to set.
     */
    public void set(@NotNull String key, @Nullable Object value) {
        if (value == null) {
            data.remove(key);
        } else {
            data.put(key, value);
        }
    }

    /**
     * Removes the value of the section.
     *
     * @param key The key of the value.
     */
    public void remove(@NotNull String key) {
        data.remove(key);
    }

    /**
     * Gets the data of the section.
     *
     * @return Returns the data of the section.
     */
    public @NotNull Map<String, Object> getData() {
        return data;
    }
}
