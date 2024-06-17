package de.obsidiancloud.common.network;

import org.jetbrains.annotations.NotNull;

/**
 * A packet listener
 *
 * @author Miles
 * @since 02.06.2024
 */
public interface PacketListener<P extends Packet> {
    /**
     * Handle a packet
     *
     * @param packet The packet
     * @param connection The connection
     */
    void handle(@NotNull P packet, @NotNull Connection connection);
}
