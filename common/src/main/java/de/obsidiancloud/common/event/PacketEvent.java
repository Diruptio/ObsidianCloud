package de.obsidiancloud.common.event;

import de.obsidiancloud.common.network.Connection;
import de.obsidiancloud.common.network.Packet;
import org.jetbrains.annotations.NotNull;

public class PacketEvent<T extends Packet> implements Event {
    private final @NotNull Connection connection;
    private final @NotNull T packet;

    /**
     * Create a new PacketEvent.
     * @param connection The connection that sent the packet.
     * @param packet The packet that was sent.
     */
    public PacketEvent(@NotNull Connection connection, @NotNull T packet) {
        this.connection = connection;
        this.packet = packet;
    }

    /**
     * Get the connection that sent the packet.
     * @return The connection that sent the packet.
     */
    public @NotNull Connection getConnection() {
        return connection;
    }

    /**
     * Get the packet that was sent.
     * @return The packet that was sent.
     */
    public @NotNull T getPacket() {
        return packet;
    }
}
