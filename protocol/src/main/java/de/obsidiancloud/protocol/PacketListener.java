package de.obsidiancloud.protocol;

import de.obsidiancloud.protocol.pipeline.ConnectionHandler;

/**
 * @author Miles
 * @since 02.06.2024
 */
public interface PacketListener<P extends Packet> {

    void handle(P packet, ConnectionHandler connection);
}
