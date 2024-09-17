package de.obsidiancloud.node.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

public class NetworkUtil {
    /**
     * Searches for a free port starting from the given minimum.
     *
     * @param minimum The minimum port
     * @return A free port
     */
    public static int getFreePort(int minimum) {
        int port = minimum;
        while (true) {
            try (ServerSocketChannel channel = ServerSocketChannel.open()) {
                channel.bind(new InetSocketAddress(port));
                break;
            } catch (IOException e) {
                if (e.getMessage().contains("Address already in use")) {
                    port++;
                } else throw new RuntimeException(e);
            }
        }
        return port;
    }
}
