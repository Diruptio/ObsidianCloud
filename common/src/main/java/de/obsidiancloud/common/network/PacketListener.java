package de.obsidiancloud.common.network;

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
    void handle(P packet, Connection connection);
}
