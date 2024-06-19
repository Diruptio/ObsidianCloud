package de.obsidiancloud.platform.network.packets;

import de.obsidiancloud.common.network.WritablePacket;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * A handshake packet from the client to the server.
 *
 * @author Miles
 * @since 08.06.2024
 */
public class S2NHandshakePacket extends WritablePacket {
    private String clusterKey;
    private String name;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, clusterKey);
        writeString(byteBuf, name);
    }

    /**
     * Sets the cluster key
     *
     * @param clusterKey The cluster key
     */
    public void setClusterKey(String clusterKey) {
        this.clusterKey = clusterKey;
    }

    /**
     * Sets the name
     *
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }
}
