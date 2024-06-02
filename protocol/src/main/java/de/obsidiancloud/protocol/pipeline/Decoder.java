package de.obsidiancloud.protocol.pipeline;

import de.obsidiancloud.protocol.Packet;
import de.obsidiancloud.protocol.packets.TestPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author Miles
 * @since 02.06.2024
 */
public class Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) {
        System.out.println("Decoding packet");
        if (byteBuf instanceof EmptyByteBuf) {
            return;
        }

        if (!byteBuf.isReadable()) {
            return;
        }
        byteBuf.readInt();

        int packetId = Packet.readVarInt(byteBuf);
        System.out.println("packetId: " + packetId);

        TestPacket packet = new TestPacket();
        packet.read(byteBuf);
        list.add(packet);
        System.out.println("decoded: " + packet.toString());
    }
}