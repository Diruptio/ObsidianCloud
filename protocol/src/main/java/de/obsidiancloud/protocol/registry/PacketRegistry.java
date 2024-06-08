package de.obsidiancloud.protocol.registry;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import de.obsidiancloud.protocol.Packet;
import de.obsidiancloud.protocol.PacketListener;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Miles
 * @since 02.06.2024
 */
public class PacketRegistry {

    private static final String PACKETS_PACKAGE = "de.obsidiancloud.protocol.packets";

    private final Map<Integer, Class<? extends Packet>> packetClasses = new ConcurrentHashMap<>();
    private final Map<Class<? extends Packet>, Integer> packetIds = new ConcurrentHashMap<>();
    private final Map<Class<? extends Packet>, Collection<PacketListener>> packetListeners = new ConcurrentHashMap<>();

    public void registerPackets() {
        registerPackets(PACKETS_PACKAGE);
    }

    public void registerPackets(String packageName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ImmutableSet<ClassPath.ClassInfo> classes;
        try {
            classes = ClassPath.from(classLoader).getTopLevelClassesRecursive(packageName);
        } catch (IOException e) {
            System.err.println("Error loading packet classes");
            e.printStackTrace();
            return;
        }
        classes.forEach(this::registerPacket);
    }

    private void registerPacket(ClassPath.ClassInfo classInfo) {
        registerPacket(classInfo.getSimpleName(), (Class<? extends Packet>) classInfo.load());
    }

    public void registerPacket(String name, Class<? extends Packet> packetClass) {
        final int packetId = name.hashCode();
        if (packetClasses.containsKey(packetId)) {
            return;
        }

        packetClasses.put(packetId, packetClass);
        packetIds.put(packetClass, packetId);
        System.out.println("Registered packet: " + packetId + " - " + packetClass);
    }

    public Optional<Class<? extends Packet>> getPacketClassById(int packetId) {
        return Optional.ofNullable(packetClasses.get(packetId));
    }

    public int getPacketIdByClass(Class<? extends Packet> packetClass) {
        return packetIds.getOrDefault(packetClass, -1);
    }

    public void registerPacketListener(PacketListener listener) {
        List<Type> list = Arrays.stream(listener.getClass().getGenericInterfaces())
                .filter(type -> ParameterizedType.class.isAssignableFrom(type.getClass()))
                .map(type -> ((ParameterizedType) type).getActualTypeArguments()[0])
                .filter(type -> Packet.class.isAssignableFrom((Class<?>) type))
                .collect(Collectors.toList());

        list.forEach(type -> packetListeners.computeIfAbsent((Class<? extends Packet>) type, (Function<Class<? extends Packet>, List<PacketListener>>)
                aClass -> new ArrayList<>()).add(listener));
    }

    public Collection<PacketListener> getPacketListeners(Class<? extends Packet> packetClass) {
        return packetListeners.getOrDefault(packetClass, new ArrayList<>());
    }
}
