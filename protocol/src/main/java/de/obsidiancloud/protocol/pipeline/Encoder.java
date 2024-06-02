package de.obsidiancloud.protocol.pipeline;

import de.obsidiancloud.protocol.NetworkHandler;
import de.obsidiancloud.protocol.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.java.Log;

/**
 * @author Miles
 * @since 02.06.2024
 */
@Log
public class Encoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf byteBuf) {
        log.info("encoding packet: " + packet.getClass().getSimpleName());
        final int packetId = NetworkHandler.getPacketRegistry().getPacketIdByClass(packet.getClass());
        if (packetId == -1) {
            log.warning("Packet " + packet.getClass() + " was not registered");
            return;
        }

        Packet.writeVarInt(packetId, byteBuf);
        packet.write(byteBuf);
        log.info("Encoded packet: " + byteBuf.toString());
    }
}