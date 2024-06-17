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

/** A generic connection. */
public class Connection {
    private @Nullable Channel channel;
    private final @NotNull Queue<Packet> backlog = new ArrayDeque<>();
    private final @NotNull List<PacketListener<? extends Packet>> packetListeners =
            new CopyOnWriteArrayList<>();

    /**
     * Send a packet.
     *
     * @param packet The packet to send
     */
    public void send(@NotNull Packet packet) {
        if (channel == null) {
            backlog.add(packet);
        } else {
            channel.writeAndFlush(packet);
        }
    }

    /**
     * Accept a packet.
     *
     * @param packet The packet to accept
     */
    @SuppressWarnings("unchecked")
    public void accept(@NotNull Packet packet) {
        for (PacketListener<? extends Packet> listener : getPacketListeners(packet.getClass())) {
            ((PacketListener<Packet>) listener).handle(packet, this);
        }
    }

    /**
     * Close the connection.
     */
    public void close() {
        if (channel != null) {
            channel.close();
        }
    }

    /**
     * Get the channel.
     *
     * @return The channel
     */
    public @Nullable Channel getChannel() {
        return channel;
    }

    /**
     * Set the channel.
     *
     * @param channel The channel
     */
    public void setChannel(@Nullable Channel channel) {
        this.channel = channel;
    }

    /**
     * Get the backlog.
     *
     * @return The backlog
     */
    public @NotNull Queue<Packet> getBacklog() {
        return backlog;
    }

    /**
     * Get the packet listeners.
     *
     * @return The packet listeners
     */
    public @NotNull List<PacketListener<? extends Packet>> getPacketListeners() {
        return packetListeners;
    }

    /**
     * Get the packet listeners for a specific packet class.
     *
     * @param <P> The packet type
     * @param packetClass The packet class
     * @return The packet listeners
     */
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

    /**
     * Add a packet listener.
     *
     * @param packetListener The packet listener to add
     */
    public void addPacketListener(@NotNull PacketListener<?> packetListener) {
        synchronized (packetListeners) {
            packetListeners.add(packetListener);
        }
    }

    /**
     * Remove a packet listener.
     *
     * @param packetListener The packet listener to remove
     */
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
