package de.obsidiancloud.common.network.packets;

import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import lombok.*;

/**
 * @author Miles
 * @since 08.06.2024
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class HandshakePacket extends Packet {

    private String id;

    @Override
    public void write(ByteBuf byteBuf) {
        writeString(id, byteBuf);
    }

    @Override
    public void read(ByteBuf byteBuf) {
        this.id = readString(byteBuf);
    }
}
