package de.obsidiancloud.node.network.packets;

import de.obsidiancloud.common.network.ReadablePacket;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * A handshake packet from the client to the server.
 *
 * @author Miles
 * @since 08.06.2024
 */
public class S2NHandshakePacket extends ReadablePacket {
    private String clusterKey;
    private String name;

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        clusterKey = readString(byteBuf);
        name = readString(byteBuf);
    }

    /**
     * Gets the cluster key
     *
     * @return The cluster key
     */
    public String getClusterKey() {
        return clusterKey;
    }

    /**
     * Gets the name
     *
     * @return The name
     */
    public String getName() {
        return name;
    }
}
