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
        final String packetName =
                NetworkHandler.getPacketRegistry().getPacketName(packet.getClass());
        if (packetName == null) {
            NetworkHandler.getLogger()
                    .severe("Packet " + packet.getClass() + " was not registered");
            return;
        }

        Packet.writeString(byteBuf, packetName);
        packet.write(byteBuf);
    }
}
