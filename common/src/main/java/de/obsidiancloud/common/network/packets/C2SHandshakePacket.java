package de.obsidiancloud.common.network.packets;

import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * A handshake packet from the client to the server.
 *
 * @author Miles
 * @since 08.06.2024
 */
public class C2SHandshakePacket extends Packet {
    private String clusterKey;
    private String name;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, clusterKey);
        writeString(byteBuf, name);
    }

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
     * Sets the cluster key
     *
     * @param clusterKey The cluster key
     */
    public void setClusterKey(String clusterKey) {
        this.clusterKey = clusterKey;
    }

    /**
     * Gets the name
     *
     * @return The name
     */
    public String getName() {
        return name;
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
