package de.obsidiancloud.protocol.packets;

import de.obsidiancloud.protocol.Packet;
import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Miles
 * @since 02.06.2024
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class TestPacket extends Packet {

    private String name;

    @Override
    public void write(ByteBuf byteBuf) {
        writeString(name, byteBuf);
    }

    @Override
    public void read(ByteBuf byteBuf) {
        int length = byteBuf.readableBytes();
        System.out.println("Packet length: " + length);

        this.name = readString(byteBuf);
    }
}
