package de.obsidiancloud.common.network.pipeline;

import de.obsidiancloud.common.network.NetworkHandler;
import de.obsidiancloud.common.network.Packet;
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
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf instanceof EmptyByteBuf) return;
        if (!byteBuf.isReadable()) return;

        byteBuf.readInt();

        String packetName = Packet.readString(byteBuf);
        if (packetName.isEmpty()) {
            return;
        }

        Class<? extends Packet> packetClass = NetworkHandler.getPacketRegistry().getPacketClass(packetName);
        if (packetClass == null) {
            NetworkHandler.getLogger().severe("Packet with name " + packetName + " was not registered");
            return;
        }

        Packet packet = packetClass.getDeclaredConstructor().newInstance();
        packet.read(byteBuf);
        list.add(packet);
    }
}
