package de.obsidiancloud.protocol.TEST;

import de.obsidiancloud.protocol.NetworkHandler;
import de.obsidiancloud.protocol.packets.TestPacket;
import de.obsidiancloud.protocol.pipeline.ConnectionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;

/**
 * @author Miles
 * @since 02.06.2024
 */
public class ServerTest {

    public static void main(String[] args) {
        ConnectionHandler nodeHandler = new ConnectionHandler("server-1", false);
        Thread t1 = new Thread(() -> {
            try {
                ServerBootstrap b = NetworkHandler.buildServerBootstrap(nodeHandler);
                ChannelFuture f = b.bind("localhost", 1337).sync();
                System.out.println("Connected");

                f.channel().closeFuture().sync();
                System.out.println("Disconnected");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t1.start();

        NetworkHandler.getPacketRegistry().registerPacketListener(new TestPacketListener());

        /*while (NetworkHandler.getConnectionRegistry().countConnections() != 3) {
            continue;
        }

        TestPacket p = new TestPacket("node-2");
        p.setName("test ;:D");
        NetworkHandler.sendPacket(p);*/
    }
}
