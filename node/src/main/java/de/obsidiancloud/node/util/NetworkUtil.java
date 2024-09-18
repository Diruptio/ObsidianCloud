package de.obsidiancloud.node.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.List;

public class NetworkUtil {
    private static final List<Integer> blockedPorts = new ArrayList<>();

    /**
     * Searches for a free port starting from the given minimum.
     *
     * @param minimum The minimum port
     * @return A free port
     */
    public static int getFreePort(int minimum) {
        int port = minimum;
        while (true) {
            if (blockedPorts.contains(port)) {
                port++;
                continue;
            }
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

    /**
     * Blocks the given port.
     *
     * @param port The port to block
     */
    public static void blockPort(int port) {
        blockedPorts.add(port);
    }

    /**
     * Unblocks the given port.
     *
     * @param port The port to unblock
     */
    public static void unblockPort(int port) {
        blockedPorts.remove(port);
    }
}
