package de.obsidiancloud.protocol.pipeline;

import de.obsidiancloud.protocol.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Miles
 * @since 02.06.2024
 */
public class Encoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf byteBuf) {
        System.out.println("encoding packet: " + packet.getClass().getSimpleName());
        Packet.writeVarInt(1, byteBuf);
        packet.write(byteBuf);
        System.out.println(byteBuf.toString());
    }
}