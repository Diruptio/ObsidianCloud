package de.obsidiancloud.common.network.pipeline;

import de.obsidiancloud.common.network.NetworkHandler;
import de.obsidiancloud.common.network.Packet;
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
        System.out.println("Encoding packet: " + packet.getClass().getSimpleName());
        final int packetId =
                NetworkHandler.getPacketRegistry().getPacketIdByClass(packet.getClass());
        if (packetId == -1) {
            System.out.println("Packet " + packet.getClass() + " was not registered");
            return;
        }

        Packet.writeVarInt(packetId, byteBuf);
        packet.write(byteBuf);
        System.out.println("Encoded packet: " + byteBuf.toString());
    }
}
