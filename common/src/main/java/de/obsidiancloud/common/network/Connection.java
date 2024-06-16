package de.obsidiancloud.common.network;

import io.netty.channel.Channel;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The connection class. */
public class Connection {
    private @Nullable Channel channel;
    private final @NotNull Queue<Packet> backlog = new ArrayDeque<>();
    private final @NotNull List<PacketListener<? extends Packet>> packetListeners =
            new CopyOnWriteArrayList<>();

    public void send(@NotNull Packet packet) {
        if (channel == null) {
            backlog.add(packet);
        } else {
            channel.writeAndFlush(packet);
        }
    }

    @SuppressWarnings("unchecked")
    public void accept(@NotNull Packet packet) {
        for (PacketListener<? extends Packet> listener : getPacketListeners(packet.getClass())) {
            ((PacketListener<Packet>) listener).handle(packet, this);
        }
    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
    }

    public @Nullable Channel getChannel() {
        return channel;
    }

    public void setChannel(@Nullable Channel channel) {
        this.channel = channel;
    }

    public @NotNull Queue<Packet> getBacklog() {
        return backlog;
    }

    public @NotNull List<PacketListener<? extends Packet>> getPacketListeners() {
        return packetListeners;
    }

    @SuppressWarnings("unchecked")
    public <P extends Packet> @NotNull List<PacketListener<P>> getPacketListeners(
            Class<P> packetClass) {
        List<PacketListener<P>> listeners = new ArrayList<>();
        for (PacketListener<?> listener : packetListeners) {
            for (Type type : listener.getClass().getGenericInterfaces()) {
                if (type instanceof ParameterizedType parameterizedType) {
                    if (parameterizedType.getRawType().equals(PacketListener.class)) {
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        if (actualTypeArguments.length > 0
                                && packetClass.isAssignableFrom(
                                        (Class<?>) actualTypeArguments[0])) {
                            listeners.add((PacketListener<P>) listener);
                            break;
                        }
                    }
                }
            }
        }
        return listeners;
    }

    public void addPacketListener(@NotNull PacketListener<?> packetListener) {
        synchronized (packetListeners) {
            packetListeners.add(packetListener);
        }
    }

    public void removePacketListener(@NotNull PacketListener<?> packetListener) {
        synchronized (packetListeners) {
            packetListeners.remove(packetListener);
        }
    }

    /**
     * Check if the connection is connected.
     *
     * @return If the connection is connected
     */
    public boolean isConnected() {
        return channel != null && channel.isActive();
    }
}
