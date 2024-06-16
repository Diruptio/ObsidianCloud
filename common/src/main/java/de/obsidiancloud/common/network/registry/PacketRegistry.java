package de.obsidiancloud.common.network.registry;

import de.obsidiancloud.common.network.Packet;
import de.obsidiancloud.common.network.packets.C2SHandshakePacket;
import de.obsidiancloud.common.network.packets.S2CHandshakePacket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Miles
 * @since 02.06.2024
 */
public class PacketRegistry {
    private final @NotNull Map<String, Class<? extends Packet>> nameToClass =
            new ConcurrentHashMap<>();
    private final @NotNull Map<Class<? extends Packet>, String> classToName =
            new ConcurrentHashMap<>();

    public void registerPackets() {
        registerPacket(C2SHandshakePacket.class);
        registerPacket(S2CHandshakePacket.class);
    }

    public void registerPacket(@NotNull Class<? extends Packet> packetClass) {
        nameToClass.put(packetClass.getSimpleName(), packetClass);
        classToName.put(packetClass, packetClass.getSimpleName());
    }

    public @Nullable String getPacketName(@NotNull Class<? extends Packet> packetClass) {
        return classToName.get(packetClass);
    }

    public @Nullable Class<? extends Packet> getPacketClass(@NotNull String packetName) {
        return nameToClass.get(packetName);
    }
}
