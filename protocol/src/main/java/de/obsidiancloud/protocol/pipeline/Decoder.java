package de.obsidiancloud.protocol.pipeline;

import de.obsidiancloud.protocol.NetworkHandler;
import de.obsidiancloud.protocol.Packet;
import de.obsidiancloud.protocol.packets.TestPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.java.Log;

import java.util.List;
import java.util.Optional;

/**
 * @author Miles
 * @since 02.06.2024
 */
@Log
public class Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf instanceof EmptyByteBuf) {
            return;
        }

        if (!byteBuf.isReadable()) {
            return;
        }
        byteBuf.readInt();

        int packetId = Packet.readVarInt(byteBuf);
        log.info("Got packetId: " + packetId);

        Optional<Class<? extends Packet>> packetClass = NetworkHandler.getPacketRegistry().getPacketClassById(packetId);
        if (packetClass.isEmpty()) {
            log.warning("No packet with id " + packetId + " registered");
            return;
        }

        Packet packet = packetClass.get().getDeclaredConstructor().newInstance();
        packet.read(byteBuf);
        list.add(packet);
        log.info("Decoded packet: " + packet.toString());
    }
}