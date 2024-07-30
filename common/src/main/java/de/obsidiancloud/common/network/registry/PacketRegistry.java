package de.obsidiancloud.common.network.registry;

import de.obsidiancloud.common.network.Packet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Miles
 * @since 02.06.2024
 */
public class PacketRegistry {
    private final @NotNull Map<String, Class<? extends Packet>> nameToClass = new ConcurrentHashMap<>();
    private final @NotNull Map<Class<? extends Packet>, String> classToName = new ConcurrentHashMap<>();

    /**
     * Register a packet class.
     *
     * @param packetClass The packet class to register
     */
    public void registerPacket(@NotNull Class<? extends Packet> packetClass) {
        nameToClass.put(packetClass.getSimpleName(), packetClass);
        classToName.put(packetClass, packetClass.getSimpleName());
    }

    /**
     * Get the packet name by the packet class.
     *
     * @param packetClass The packet class
     * @return The packet name
     */
    public @Nullable String getPacketName(@NotNull Class<? extends Packet> packetClass) {
        return classToName.get(packetClass);
    }

    /**
     * Get the packet class by the packet name.
     *
     * @param packetName The packet name
     * @return The packet class
     */
    public @Nullable Class<? extends Packet> getPacketClass(@NotNull String packetName) {
        return nameToClass.get(packetName);
    }
}
