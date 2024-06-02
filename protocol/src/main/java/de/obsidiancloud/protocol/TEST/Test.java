package de.obsidiancloud.protocol.TEST;

import de.obsidiancloud.protocol.NetworkHandler;
import de.obsidiancloud.protocol.packets.TestPacket;
import de.obsidiancloud.protocol.pipeline.ConnectionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;

import java.util.TimerTask;

/**
 * @author Miles
 * @since 02.06.2024
 */
public class Test {

    public static void main(String[] args) {
        ConnectionHandler node1Handler = new ConnectionHandler("node-1", false);
        Thread t = new Thread(() -> {
            try {
                ServerBootstrap b = NetworkHandler.buildServerBootstrap(node1Handler);
                ChannelFuture f = b.bind("localhost", 1337).sync();
                System.out.println("Connected");

                f.channel().closeFuture().sync();
                System.out.println("Disconnected");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();

        NetworkHandler.getPacketRegistry().registerPackets();
        NetworkHandler.getPacketRegistry().registerPacketListener(new TestPacketListener());

        ConnectionHandler lobby1Handler = NetworkHandler.initializeClientConnection("lobby-1", "localhost", 1337);
        ConnectionHandler lobby2Handler = NetworkHandler.initializeClientConnection("lobby-2", "localhost", 1337);


        TestPacket p = new TestPacket();
        p.setName("test-packet?!");
        NetworkHandler.sendPacket("lobby-2", p);
    }
}
