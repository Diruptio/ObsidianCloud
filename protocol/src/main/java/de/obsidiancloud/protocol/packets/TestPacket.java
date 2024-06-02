package de.obsidiancloud.protocol.packets;

import de.obsidiancloud.protocol.Packet;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

/**
 * @author Miles
 * @since 02.06.2024
 */
public class TestPacket extends Packet {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TestPacket{" +
                "name='" + name + '\'' +
                '}';
    }

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
