package de.obsidiancloud.common.network;

import de.obsidiancloud.common.network.pipeline.ConnectionHandler;

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
     * @param packet the packet
     * @param connection the connection
     */
    void handle(P packet, ConnectionHandler connection);
}
