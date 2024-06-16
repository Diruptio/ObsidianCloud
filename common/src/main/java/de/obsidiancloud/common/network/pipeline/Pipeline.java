package de.obsidiancloud.common.network.pipeline;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * The pipeline for the network.
 *
 * @author Miles
 * @since 02.06.2024
 */
public class Pipeline {

    private static final String FRAME_DECODER = "frameDecoder";
    private static final String DECODER = "decoder";
    private static final String FRAME_PREPENDER = "framePrepender";
    private static final String ENCODER = "encoder";
    private static final String HANDLER = "handler";

    public static void prepare(Channel channel, ChannelHandler handler) {
        channel.pipeline()
                .addLast(FRAME_DECODER, new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));
        channel.pipeline().addLast(DECODER, new Decoder());
        channel.pipeline().addLast(FRAME_PREPENDER, new LengthFieldPrepender(4));
        channel.pipeline().addLast(ENCODER, new Encoder());
        channel.pipeline().addLast(HANDLER, handler);
    }
}
